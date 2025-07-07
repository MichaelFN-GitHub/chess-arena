package com.MichaelFN.chess.v5.search;

import com.MichaelFN.chess.v5.Utils;
import com.MichaelFN.chess.v5.move.Move;
import com.MichaelFN.chess.v5.move.MoveGenerator;
import com.MichaelFN.chess.v5.board.Board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.MichaelFN.chess.common.Constants.DEBUG_SEARCH;

public class Searcher {
    private final MoveGenerator moveGenerator;
    private final Evaluator evaluator;
    private final TranspositionTable transpositionTable;

    private int nodesSearched;
    private int branchesPruned;
    private boolean timeIsUp;

    // Save principal variation for every depth
    private static final int MAX_DEPTH = 64;
    private final int[][] pvTable = new int[MAX_DEPTH][MAX_DEPTH];
    private final int[] pvLength = new int[MAX_DEPTH];

    private static final int CHECKMATE_SCORE = 99999999;
    private static final int DRAW_SCORE = 0;

    public Searcher(Evaluator evaluator) {
        this.moveGenerator = new MoveGenerator();
        this.evaluator = evaluator;
        this.transpositionTable = new TranspositionTable(256);
    }

    public int negamax(Board board, int maxDepth, long timeMS) {
        nodesSearched = 0;
        branchesPruned = 0;
        timeIsUp = false;
        clearPrincipalVariation();

        int bestMove = 0;
        int bestScore = 0;

        long startTime = System.currentTimeMillis();
        long endTime = startTime + timeMS;

        // Iterative deepening
        for (int depth = 1; depth <= maxDepth; depth++) {
            long iterStartTime = System.currentTimeMillis();
            int nodesBefore = nodesSearched;

            int alpha = Integer.MIN_VALUE + 1;
            int beta = Integer.MAX_VALUE - 1;

            int score = negamax(board, depth, alpha, beta, 0, endTime);

            if (timeIsUp) break;    // Don't overwrite best score or move if the search was interrupted

            bestScore = score;
            TTEntry ttEntry = transpositionTable.get(board.hashKey);
            bestMove = ttEntry == null ? 0 : ttEntry.bestMove;

            if (DEBUG_SEARCH) {
                long iterEndTime = System.currentTimeMillis();
                long timeSpent = Math.max(iterEndTime - iterStartTime, 1);
                long nodesThisDepth = nodesSearched - nodesBefore;
                long nodesPerSecond = (nodesThisDepth * 1000) / timeSpent;

                System.out.print("info depth " + depth +
                        " score cp " + bestScore +
                        " nodes " + nodesThisDepth +
                        " nps " + nodesPerSecond +
                        " time " + timeSpent +
                        " pv ");
                for (int move : getPrincipalVariation(board, depth)) System.out.print(Move.toString(move) + " ");
                System.out.println();
            }
        }

        if (DEBUG_SEARCH) {
            System.out.println("Nodes searched: " + nodesSearched);
            System.out.println("Branches pruned: " + branchesPruned);
            System.out.println("Time used: " + (System.currentTimeMillis() - startTime));
            System.out.println("Best score: " + bestScore);
            System.out.println("Best move: " + Move.toString(bestMove));
            System.out.println(transpositionTable);
        }
        return bestMove;
    }

    private int negamax(Board board, int depth, int alpha, int beta, int ply, long endTime) {
        // Check for timeout before any computation
        if (System.currentTimeMillis() > endTime) {
            timeIsUp = true;
            return 0;
        }

        nodesSearched++;

        // Repetition, fifty move rule, insufficient material
        if (board.isRepetition() || board.fiftyMoveRule() || board.isInsufficientMaterial()) return DRAW_SCORE;

        // Transposition table lookup
        long hashKey = board.hashKey;
        TTEntry ttEntry = transpositionTable.get(hashKey);
        int ttMove = 0;

        if (ttEntry != null && ttEntry.depth >= depth) {
            ttMove = ttEntry.bestMove;

            if (ttEntry.flag == TTEntry.EXACT) {
                return ttEntry.score;
            } else if (ttEntry.flag == TTEntry.LOWERBOUND && ttEntry.score > alpha) {
                alpha = ttEntry.score;
            } else if (ttEntry.flag == TTEntry.UPPERBOUND && ttEntry.score < beta) {
                beta = ttEntry.score;
            }

            if (alpha >= beta) {
                branchesPruned++;
                return ttEntry.score;
            }
        }

        // Reached max depth: evaluate using quiescence search
        if (depth == 0) {
            return quiescence(board, alpha, beta, ply + 1, endTime);
        }

        // Generate and order all legal moves
        moveGenerator.generateLegalMoves(board, ply);
        int moveCount = moveGenerator.legalMoveCounts[ply];

        // No legal moves
        if (moveCount == 0) {
            if (Utils.isInCheck(board, board.playerToMove)) {
                // Checkmate
                return -CHECKMATE_SCORE + ply;
            }
            // Stalemate
            return DRAW_SCORE;
        }

        int[] legalMoves = moveGenerator.legalMoves[ply];
        int pvMove = pvLength[ply] > 0 ? pvTable[ply][0] : 0;
        MoveOrdering.orderMoves(moveCount, legalMoves, pvMove, ttMove, board);

        int maxScore = Integer.MIN_VALUE;
        int bestMoveAtThisNode = 0;
        int originalAlpha = alpha;

        for (int i = 0; i < moveCount; i++) {
            if (System.currentTimeMillis() > endTime) {
                timeIsUp = true;
                return 0;
            }

            int move = legalMoves[i];

            board.makeMove(move);
            int score = -negamax(board, depth - 1, -beta, -alpha, ply + 1, endTime);
            board.unmakeMove();

            if (timeIsUp) break;

            if (score > maxScore) {
                maxScore = score;
                bestMoveAtThisNode = move;
            }

            if (score > alpha) {
                alpha = score;
                updatePrincipalVariation(ply, move);
            }

            // Beta cutoff
            if (alpha >= beta) {
                branchesPruned++;
                break;
            }
        }

        if (!timeIsUp) {
            // Store result in transposition table
            storeResult(hashKey, depth, maxScore, bestMoveAtThisNode, originalAlpha, beta);
        }

        return maxScore;
    }

    private int quiescence(Board board, int alpha, int beta, int ply, long endTime) {
        if (System.currentTimeMillis() > endTime) {
            timeIsUp = true;
            return 0;
        }

        nodesSearched++;

        // Evaluate the position without making any further captures ("stand pat").
        // This serves as the baseline score if we choose to do nothing.
        // Helps prune bad capture sequences and avoid horizon effect.
        // The horizon effect: Engine can't spot imminent threat because search has been stopped just before.
        int standPat = evaluator.evaluate(board);

        if (standPat >= beta) return beta;
        if (standPat > alpha) alpha = standPat;

        // Generate and order all legal captures
        moveGenerator.generateLegalCaptures(board, ply);
        int captureCount = moveGenerator.legalMoveCounts[ply];
        int[] captures = moveGenerator.legalMoves[ply];
        MoveOrdering.orderMoves(captureCount, captures, 0, 0, board);  // Not sure if this matters much for performance

        for (int i = 0; i < captureCount; i++) {
            if (System.currentTimeMillis() > endTime) {
                timeIsUp = true;
                return 0;
            }

            int move = captures[i];

            board.makeMove(move);
            int score = -quiescence(board, -beta, -alpha, ply + 1, endTime);
            board.unmakeMove();

            if (timeIsUp) break;

            if (score >= beta) {
                branchesPruned++;
                return beta;
            }
            if (score > alpha) alpha = score;
        }

        return alpha;
    }

    private void storeResult(long hashKey, int depth, int maxScore, int bestMoveAtThisNode, int originalAlpha, int beta) {
        int flag;
        if (maxScore <= originalAlpha) {
            flag = TTEntry.UPPERBOUND;
        } else if (maxScore >= beta) {
            flag = TTEntry.LOWERBOUND;
        } else {
            flag = TTEntry.EXACT;
        }
        transpositionTable.put(hashKey, depth, maxScore, flag, bestMoveAtThisNode);
    }

    private void updatePrincipalVariation(int ply, int move) {

        // Add move at this ply
        pvTable[ply][0] = move;

        // Append the rest of the moves from the next ply
        int nextPlyPVLength = pvLength[ply + 1];
        if (nextPlyPVLength >= 0) System.arraycopy(pvTable[ply + 1], 0, pvTable[ply], 1, nextPlyPVLength);
        pvLength[ply] = nextPlyPVLength + 1;
    }

    private List<Integer> getPrincipalVariation(Board board, int depth) {
        List<Integer> PV = new ArrayList<>();

        // Walk through transposition table and append best moves
        int counter = 0;
        for (int i = 0; i < depth; i++) {
            TTEntry ttEntry = transpositionTable.get(board.hashKey);
            if (ttEntry == null || ttEntry.bestMove == 0) {
                break;
            }

            int move = ttEntry.bestMove;
            PV.add(move);
            board.makeMove(move);
            counter++;
        }

        // Reset board state
        for (int i = 0; i < counter; i++) {
            board.unmakeMove();
        }

        return PV;
    }

    private void clearPrincipalVariation() {
        for (int i = 0; i < pvTable.length; i++) {
            Arrays.fill(pvTable[i], 0);
            pvLength[i] = 0;
        }
    }

    public void clearTranspositionTable() {
        transpositionTable.clear();
    }

    public void stop() {
        timeIsUp = true;
    }
}
