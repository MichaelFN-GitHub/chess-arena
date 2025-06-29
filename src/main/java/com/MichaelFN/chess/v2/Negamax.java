package com.MichaelFN.chess.v2;

import com.MichaelFN.chess.interfaces.NormalEvaluator;
import com.MichaelFN.chess.v1.BoardState;
import com.MichaelFN.chess.v1.Move;
import com.MichaelFN.chess.v1.MoveGenerator;

import java.util.List;

public class Negamax {
    private final NormalEvaluator evaluator;
    private final TranspositionTable transpositionTable;

    private int nodesSearched;
    private boolean isTimeUp;

    // Save principal variation for every depth
    private static final int MAX_DEPTH = 64;
    private final Move[][] pvTable = new Move[MAX_DEPTH][MAX_DEPTH];
    private final int[] pvLength = new int[MAX_DEPTH];

    public Negamax(NormalEvaluator evaluator) {
        this.evaluator = evaluator;
        this.transpositionTable = new TranspositionTable();
    }

    public Move findBestMove(BoardState boardState, int maxDepth, long time) {
        nodesSearched = 0;
        isTimeUp = false;

        Move bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        long startTime = System.currentTimeMillis();
        long endTime = startTime + time;
        int ply = 0;

        // Generate all legal moves
        List<Move> legalMoves = MoveGenerator.generateLegalMoves(boardState);

        // Iterative deepening from 1 to maxDepth
        for (int depth = 1; depth <= maxDepth; depth++) {
            int bestScoreThisDepth = Integer.MIN_VALUE;
            Move bestMoveThisDepth = null;

            int alpha = Integer.MIN_VALUE + 1;
            int beta = Integer.MAX_VALUE - 1;

            // Reorder moves based on principal variation at every max depth
            Move pvMove = pvLength[ply] > 0 ? pvTable[ply][ply] : null;
            MoveOrdering.orderMoves(legalMoves, pvMove);

            // Try all legal moves at this depth
            for (Move move : legalMoves) {
                boardState.makeMove(move);
                int score = -negamax(boardState, depth - 1, alpha, beta, ply + 1, endTime);
                boardState.unmakeMove();

                if (isTimeUp) break;

                if (score > bestScoreThisDepth) {
                    bestScoreThisDepth = score;
                    bestMoveThisDepth = move;
                    updatePrincipalVariation(ply, move);
                }
            }

            if (isTimeUp) break;
            bestScore = bestScoreThisDepth;
            bestMove = bestMoveThisDepth;
            System.out.println("Depth " + depth + " searched. Current best variation: " + getPrincipalVariation());
        }

        System.out.println("Nodes searched: " + nodesSearched);
        System.out.println("Time used: " + (System.currentTimeMillis() - startTime));
        System.out.println("Best score: " + bestScore);
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

        if (ttEntry != null && ttEntry.depth >= depth) {
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

        int maxScore = Integer.MIN_VALUE;
        int originalAlpha = alpha;

        // Generate and order all legal moves
        List<Move> legalMoves = MoveGenerator.generateLegalMoves(boardState);
        Move pvMove = pvLength[ply] > 0 ? pvTable[ply][ply] : null;
        MoveOrdering.orderMoves(legalMoves, pvMove);

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

        // Store result in transposition table
        if (!isTimeUp) {
            int flag;
            if (maxScore <= originalAlpha) {
                flag = TranspositionTable.Entry.UPPERBOUND;
            } else if (maxScore >= beta) {
                flag = TranspositionTable.Entry.LOWERBOUND;
            } else {
                flag = TranspositionTable.Entry.EXACT;
            }
            transpositionTable.put(hashKey, new TranspositionTable.Entry(depth, maxScore, flag));
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
        MoveOrdering.orderMoves(captures, null);  // Not sure if this matters much for performance

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

    private void updatePrincipalVariation(int ply, Move move) {
        pvTable[ply][ply] = move;
        for (int i = ply + 1; i < pvLength[ply + 1] + ply + 1; i++) {
            pvTable[ply][i] = pvTable[ply + 1][i];
        }
        pvLength[ply] = pvLength[ply + 1] + 1;
    }

    private String getPrincipalVariation() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pvLength[0]; i++) {
            sb.append(pvTable[0][i]);
            if (i < pvLength[0] - 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }
}
