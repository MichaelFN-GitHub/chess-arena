package com.MichaelFN.chess.v6;

import com.MichaelFN.chess.v5.Utils;
import com.MichaelFN.chess.v5.board.Board;
import com.MichaelFN.chess.v5.move.Move;
import com.MichaelFN.chess.v5.move.MoveGenerator;
import com.MichaelFN.chess.v5.search.Evaluator;
import com.MichaelFN.chess.v5.search.TTEntry;
import com.MichaelFN.chess.v5.search.TranspositionTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.MichaelFN.chess.common.Constants.DEBUG_SEARCH;
import static com.MichaelFN.chess.v5.Constants.*;

public class Searcher {
    private static final int MAX_DEPTH = 64;
    private static final int CHECKMATE_SCORE = 99999999;
    private static final int DRAW_SCORE = 1;

    private final MoveGenerator moveGenerator = new MoveGenerator();
    private final Evaluator evaluator;
    private final TranspositionTable transpositionTable = new TranspositionTable(256);

    private int nodesSearched;
    private int branchesPruned;
    private boolean timeIsUp;

    // Save principal variation for every depth
    private final int[][] pvTable = new int[MAX_DEPTH][MAX_DEPTH];
    private final int[] pvLength = new int[MAX_DEPTH];

    // Killer moves are quiet moves that caused a beta-cutoff (good quiet moves)
    private final int[][] killerMoves = new int[MAX_PLY][2];  // store 2 killers per ply

    // Remembers good quiet moves across all positions
    private final int[][] historyHeuristic = new int[7][64];

    public Searcher(Evaluator evaluator) {
        this.evaluator = evaluator;
    }

    public int negamax(Board board, int maxDepth, long timeMS) {
        nodesSearched = 0;
        branchesPruned = 0;
        timeIsUp = false;

        clearPrincipalVariation();
        clearKillerMoves();
        clearHistoryHeuristics();
        //clearTranspositionTable();  // For some reason the engine won't detect repetition sometimes if I dont clear this...

        int bestMove = 0;
        int bestScore = 0;

        long startTime = System.currentTimeMillis();
        long endTime = startTime + timeMS;

        // Aspiration window
        int guess = 0;
        int window = 50;

        // Iterative deepening
        for (int depth = 1; depth <= maxDepth; depth++) {
            long iterStartTime = System.currentTimeMillis();
            long nodesBefore = nodesSearched;

            int alpha = guess - window;
            int beta = guess + window;

            int score = negamax(board, depth, alpha, beta, 0, endTime, true);
            if (timeIsUp) break;

            // If score outside aspiration window, re-search with full window
            if (score <= alpha) {
                alpha = Integer.MIN_VALUE + 1;
                beta = guess + window;
                score = negamax(board, depth, alpha, beta, 0, endTime, true);
            } else if (score >= beta) {
                alpha = guess - window;
                beta = Integer.MAX_VALUE - 1;
                score = negamax(board, depth, alpha, beta, 0, endTime, true);
            }

            if (timeIsUp) break;

            guess = score;
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
                List<Integer> pvMoves = getPrincipalVariation(0);
                int count = Math.min(pvMoves.size(), depth);
                for (int i = 0; i < count; i++) System.out.print(Move.toString(pvMoves.get(i)) + " ");
                System.out.println();
            }
        }

        if (DEBUG_SEARCH) {
            System.out.println("Nodes searched: " + nodesSearched);
            System.out.println("Branched pruned: " + branchesPruned);
            System.out.println("Time used: " + (System.currentTimeMillis() - startTime));
            System.out.println("Best score: " + bestScore);
            System.out.println("Best move: " + Move.toString(bestMove));
            System.out.println(transpositionTable);
        }
        return bestMove;
    }

    private int negamax(Board board, int depth, int alpha, int beta, int ply, long endTime, boolean isPv) {
        // Check for timeout before any computation
        if ((nodesSearched & 2048) == 0 && System.currentTimeMillis() > endTime) {
            timeIsUp = true;
            return 0;
        }

        nodesSearched++;

        // Repetition, fifty move rule, insufficient material
        if (board.isRepetition() || board.fiftyMoveRule() || board.isInsufficientMaterial()) {
            return DRAW_SCORE;
        }

        // Transposition table lookup
        long hashKey = board.hashKey;
        TTEntry ttEntry = transpositionTable.get(hashKey);
        int ttMove = 0;

        if (!isPv && ttEntry != null) {
            ttMove = ttEntry.bestMove;

            if (ttEntry.depth >= depth) {
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
        }

        // Reached max depth: evaluate using quiescence search
        if (depth <= 0) {
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

        // Null move pruning
        boolean isInCheck = Utils.isInCheck(board, board.playerToMove);
        if (depth >= 3 &&
                !isInCheck &&
                (board.pieces[WHITE][QUEEN] | board.pieces[BLACK][QUEEN]) != 0 &&
                !timeIsUp) {

            board.makeNullMove();

            int R = (depth >= 6) ? 3 : 2;
            int score = -negamax(board, depth - R - 1, -beta, -beta + 1, ply + 1, endTime, false);

            board.unmakeNullMove();

            if (score >= beta) {
                branchesPruned++;
                return beta;
            }
        }

        // Move ordering
        int[] legalMoves = moveGenerator.legalMoves[ply];
        int pvMove = pvLength[ply] > 0 ? pvTable[ply][0] : 0;
        MoveOrdering.orderMoves(moveCount, legalMoves, pvMove, killerMoves, ttMove, historyHeuristic, board, ply);

        int maxScore = Integer.MIN_VALUE;
        int bestMoveAtThisNode = 0;
        int originalAlpha = alpha;

        boolean firstSearch = true;
        boolean updatePv = false;

        for (int i = 0; i < moveCount; i++) {
            if ((nodesSearched & 2048) == 0 && System.currentTimeMillis() > endTime) {
                timeIsUp = true;
                return 0;
            }

            int move = legalMoves[i];

            // Futility pruning
            if (!firstSearch) {
                if ((depth == 1) &&
                        !isInCheck &&
                        !Move.isCapture(move) &&
                        !Move.isPromotion(move)) {

                    int staticEval = evaluator.evaluate(board);
                    int margin = 150;

                    if (staticEval + margin <= alpha) {
                        branchesPruned++;
                        continue;
                    }
                }
            }

            board.makeMove(move);

            // Check extension
            int extension = Utils.isInCheck(board, board.playerToMove) ? 1 : 0;

            // Principal variation search
            int score;
            if (firstSearch) {
                // Full window search on first move
                score = -negamax(board, depth - 1 + extension, -beta, -alpha, ply + 1, endTime, isPv);
                firstSearch = false;
                updatePv = true;

            } else {

                // Late move reduction (very conservative)
                int newDepth = (i >= 6 && depth >= 5 && !Move.isCapture(move) && !Move.isPromotion(move) && move != ttMove && ply > 1) ?
                        depth - 2 : // Reduce depth
                        depth - 1;  // Don't reduce

                // Null window search
                score = -negamax(board, newDepth, -alpha - 1, -alpha, ply + 1, endTime, false);
                updatePv = false;

                // Re-search only if score suggests move is better, and window is wide enough
                if (score > alpha && score < beta) {
                    score = -negamax(board, depth - 1 + extension, -beta, -alpha, ply + 1, endTime, true);
                    updatePv = true;
                }
            }

            board.unmakeMove();

            if (timeIsUp) break;

            if (score > maxScore) {
                maxScore = score;
                bestMoveAtThisNode = move;
            }

            if (score > alpha) {
                alpha = score;
                if (updatePv) updatePrincipalVariation(ply, move);
            }

            // Beta cutoff
            if (alpha >= beta) {
                branchesPruned++;

                // Store killer move
                if (!Move.isCapture(bestMoveAtThisNode) && !Move.isPromotion(bestMoveAtThisNode)) {
                    if (killerMoves[ply][0] != bestMoveAtThisNode) {
                        killerMoves[ply][1] = killerMoves[ply][0];
                        killerMoves[ply][0] = bestMoveAtThisNode;
                    }
                }

                // Update history heuristic
                int piece = board.pieceAtSquare[Move.getFrom(bestMoveAtThisNode)];
                int to = Move.getTo(bestMoveAtThisNode);
                historyHeuristic[piece][to] += depth * depth;

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
        // Avoid some overhead
        if ((nodesSearched & 2048) == 0 && System.currentTimeMillis() > endTime) {
            timeIsUp = true;
            return 0;
        }

        nodesSearched++;

        // Evaluate the position without making any further captures ("stand pat").
        // This serves as the baseline score if we choose to do nothing.
        // Helps prune bad capture sequences and avoid horizon effect.
        // The horizon effect: Engine can't spot imminent threat because search has been stopped just before.
        int standPat = evaluator.evaluate(board);

        //if (standPat + DELTA_MARGIN < alpha) return alpha;
        if (standPat >= beta) return beta;
        if (standPat > alpha) alpha = standPat;

        // Generate legal captures
        moveGenerator.generateLegalCaptures(board, ply);
        int captureCount = moveGenerator.legalMoveCounts[ply];

        // No legal captures
        if (captureCount == 0) return alpha;

        // Order all legal captures (TODO: Maybe include promotions and other important moves like checks)
        int[] captures = moveGenerator.legalMoves[ply];
        MoveOrdering.orderMoves(captureCount, captures, 0, null, 0, null, board, ply);

        for (int i = 0; i < captureCount; i++) {
            if ((nodesSearched & 2048) == 0 && System.currentTimeMillis() > endTime) {
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
        pvTable[ply][0] = move;
        int nextPlyPVLength = (ply + 1 < pvLength.length) ? pvLength[ply + 1] : 0;
        if (nextPlyPVLength > 0) {
            System.arraycopy(pvTable[ply + 1], 0, pvTable[ply], 1, nextPlyPVLength);
        }
        pvLength[ply] = nextPlyPVLength + 1;
    }

    private List<Integer> getPrincipalVariation(int ply) {
        int length = pvLength[ply];
        List<Integer> pvMoves = new ArrayList<>(length);

        for (int i = 0; i < length; i++) {
            pvMoves.add(pvTable[ply][i]);
        }

        return pvMoves;
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

    public void clearKillerMoves() {
        for (int ply = 0; ply < MAX_PLY; ply++) {
            killerMoves[ply][0] = 0;
            killerMoves[ply][1] = 0;
        }
    }

    public void clearHistoryHeuristics() {
        Arrays.stream(historyHeuristic).forEach(row -> Arrays.fill(row, 0));
    }

    public void clear() {
        clearTranspositionTable();
        clearPrincipalVariation();
        clearKillerMoves();
    }
}
