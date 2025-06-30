package com.MichaelFN.chess.v2;

import com.MichaelFN.chess.interfaces.NormalEvaluator;
import com.MichaelFN.chess.v1.BoardState;
import com.MichaelFN.chess.v1.Move;
import com.MichaelFN.chess.v1.MoveGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.MichaelFN.chess.common.Constants.DEBUG_SEARCH;

public class Negamax {
    private final NormalEvaluator evaluator;
    private final TranspositionTable transpositionTable = new TranspositionTable(256);

    private int nodesSearched;
    private boolean isTimeUp;

    // Save principal variation for every depth
    private static final int MAX_DEPTH = 64;
    private final Move[][] pvTable = new Move[MAX_DEPTH][MAX_DEPTH];
    private final int[] pvLength = new int[MAX_DEPTH];

    public Negamax(NormalEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    public Move findBestMove(BoardState boardState, int maxDepth, long time) {
        nodesSearched = 0;
        isTimeUp = false;

        Move bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        long startTime = System.currentTimeMillis();
        long endTime = startTime + time;

        clearPrincipalVariation();

        // Iterative deepening
        for (int depth = 1; depth <= maxDepth; depth++) {

            int alpha = Integer.MIN_VALUE + 1;  // To avoid integer overflow
            int beta = Integer.MAX_VALUE - 1;

            int score = negamax(boardState, depth, alpha, beta, 0, endTime);

            if (isTimeUp) break;

            bestScore = score;
            TranspositionTable.Entry ttEntry = transpositionTable.get(boardState.getKey());
            bestMove = ttEntry == null ? null : ttEntry.bestMove;

            if (DEBUG_SEARCH) {
                // Print principal variation
                System.out.print("Depth " + depth + " searched. Current best variation: ");
                for (Move move : getPrincipalVariation(boardState, depth)) System.out.print(move + " ");
                System.out.println();
            }
        }

        if (DEBUG_SEARCH) {
            System.out.println("Nodes searched: " + nodesSearched);
            System.out.println("Time used: " + (System.currentTimeMillis() - startTime));
            System.out.println("Best score: " + bestScore);
            System.out.println("Best move: " + bestMove);
            System.out.println(transpositionTable);
        }
        return bestMove;
    }

    private int negamax(BoardState boardState, int depth, int alpha, int beta, int ply, long endTime) {
        // Check for timeout before any computation
        if (System.currentTimeMillis() > endTime) {
            isTimeUp = true;
            return 0;
        }

        nodesSearched++;

        // Transposition table lookup
        long hashKey = boardState.getKey();
        TranspositionTable.Entry ttEntry = transpositionTable.get(hashKey);
        Move ttMove = null;

        if (ttEntry != null && ttEntry.depth >= depth) {
            ttMove = ttEntry.bestMove;

            if (ttEntry.flag == TranspositionTable.Entry.EXACT) {
                return ttEntry.score;
            } else if (ttEntry.flag == TranspositionTable.Entry.LOWERBOUND && ttEntry.score > alpha) {
                alpha = ttEntry.score;
            } else if (ttEntry.flag == TranspositionTable.Entry.UPPERBOUND && ttEntry.score < beta) {
                beta = ttEntry.score;
            }

            if (alpha >= beta) {
                return ttEntry.score;
            }
        }

        // Check for checkmate or stalemate
        if (boardState.isGameOver()) {
            if (boardState.isCheckmate()) return -9999999 - depth;

            // Stalemate
            return 1;   // Avoid stalemate in equals positions
        }

        // Reached max depth: evaluate using quiescence search
        if (depth == 0) {
            return quiescence(boardState, alpha, beta, endTime);
        }

        // Generate and order all legal moves
        List<Move> legalMoves = MoveGenerator.generateLegalMoves(boardState);
        Move pvMove = pvLength[ply] > 0 ? pvTable[ply][0] : null;
        MoveOrdering.orderMoves(legalMoves, pvMove, ttMove);

        int maxScore = Integer.MIN_VALUE;
        Move bestMoveAtThisNode = null;
        int originalAlpha = alpha;

        for (Move move : legalMoves) {
            if (System.currentTimeMillis() > endTime) {
                isTimeUp = true;
                return 0;
            }

            boardState.makeMove(move);
            int score = -negamax(boardState, depth - 1, -beta, -alpha, ply + 1, endTime);
            boardState.unmakeMove();

            if (isTimeUp) break;

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
                break;
            }
        }

        if (!isTimeUp) {
            // Store result in transposition table
            storeResult(hashKey, depth, maxScore, bestMoveAtThisNode, originalAlpha, beta);
        }

        return maxScore;
    }

    private int quiescence(BoardState boardState, int alpha, int beta, long endTime) {
        if (System.currentTimeMillis() > endTime) {
            isTimeUp = true;
            return 0;
        }

        nodesSearched++;

        // Evaluate the position without making any further captures ("stand pat").
        // This serves as the baseline score if we choose to do nothing.
        // Helps prune bad capture sequences and avoid horizon effect.
        // The horizon effect: Engine can't spot imminent threat because search has been stopped just before.
        int standPat = evaluator.evaluate(boardState);

        if (standPat >= beta) return beta;
        if (standPat > alpha) alpha = standPat;

        // Generate and order all legal captures
        List<Move> captures = MoveGenerator.generateCaptures(boardState);
        MoveOrdering.orderMoves(captures, null, null);  // Not sure if this matters much for performance

        for (Move move : captures) {
            if (System.currentTimeMillis() > endTime) {
                isTimeUp = true;
                return 0;
            }

            boardState.makeMove(move);
            int score = -quiescence(boardState, -beta, -alpha, endTime);
            boardState.unmakeMove();

            if (isTimeUp) break;

            if (score >= beta) return beta;
            if (score > alpha) alpha = score;
        }

        return alpha;
    }

    private void storeResult(long hashKey, int depth, int maxScore, Move bestMoveAtThisNode, int originalAlpha, int beta) {
        int flag;
        if (maxScore <= originalAlpha) {
            flag = TranspositionTable.Entry.UPPERBOUND;
        } else if (maxScore >= beta) {
            flag = TranspositionTable.Entry.LOWERBOUND;
        } else {
            flag = TranspositionTable.Entry.EXACT;
        }
        transpositionTable.put(hashKey, depth, maxScore, flag, bestMoveAtThisNode);
    }

    private void updatePrincipalVariation(int ply, Move move) {

        // Add move at this ply
        pvTable[ply][0] = move;

        // Append the rest of the moves from the next ply
        int nextPlyPVLength = pvLength[ply + 1];
        for (int i = 0; i < nextPlyPVLength; i++) {
            pvTable[ply][i + 1] = pvTable[ply + 1][i];
        }
        pvLength[ply] = nextPlyPVLength + 1;
    }

    private List<Move> getPrincipalVariation(BoardState boardState, int depth) {
        List<Move> PV = new ArrayList<>();

        // Walk through transposition table and append best moves
        int counter = 0;
        for (int i = 0; i < depth; i++) {
            TranspositionTable.Entry ttEntry = transpositionTable.get(boardState.getKey());
            if (ttEntry == null || ttEntry.bestMove == null) {
                break;
            }

            Move move = ttEntry.bestMove;
            PV.add(move);
            boardState.makeMove(move);
            counter++;
        }

        // Reset board state
        for (int i = 0; i < counter; i++) {
            boardState.unmakeMove();
        }

        return PV;
    }

    private void clearPrincipalVariation() {
        for (int i = 0; i < pvTable.length; i++) {
            Arrays.fill(pvTable[i], null);
            pvLength[i] = 0;
        }
    }

    public void clearTranspositionTable() {
        transpositionTable.clear();
    }
}
