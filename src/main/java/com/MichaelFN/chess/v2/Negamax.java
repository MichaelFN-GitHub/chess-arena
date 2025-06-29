package com.MichaelFN.chess.v2;

import com.MichaelFN.chess.interfaces.NormalEvaluator;
import com.MichaelFN.chess.v1.BoardState;
import com.MichaelFN.chess.v1.Move;
import com.MichaelFN.chess.v1.MoveGenerator;

import java.util.List;

public class Negamax {
    private final NormalEvaluator evaluator;

    private int nodesSearched;
    private boolean isTimeUp;

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

        // Generate and order all legal moves
        List<Move> legalMoves = MoveGenerator.generateLegalMoves(boardState);
        MoveOrdering.orderMoves(legalMoves);

        // Iterative deepening from 1 to maxDepth
        for (int depth = 1; depth <= maxDepth; depth++) {
            int bestScoreThisDepth = Integer.MIN_VALUE;
            Move bestMoveThisDepth = null;

            int alpha = Integer.MIN_VALUE + 1;
            int beta = Integer.MAX_VALUE - 1;

            // Try all legal moves at this depth
            for (Move move : legalMoves) {
                boardState.makeMove(move);
                int score = -negamax(boardState, depth - 1, alpha, beta, endTime);
                boardState.unmakeMove();

                if (isTimeUp) break;

                if (score > bestScoreThisDepth) {
                    bestScoreThisDepth = score;
                    bestMoveThisDepth = move;
                }
            }

            if (isTimeUp) break;
            bestScore = bestScoreThisDepth;
            bestMove = bestMoveThisDepth;
            System.out.println("Depth " + depth + " searched. Current best move: " + bestMove);
        }

        System.out.println("Nodes searched: " + nodesSearched);
        System.out.println("Time used: " + (System.currentTimeMillis() - startTime));
        System.out.println("Best score: " + bestScore);
        return bestMove;
    }

    private int negamax(BoardState boardState, int depth, int alpha, int beta, long endTime) {
        // Check for timeout before any computation
        if (System.currentTimeMillis() > endTime) {
            isTimeUp = true;
            return 0;
        }

        nodesSearched++;

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

        // Generate and order all legal moves
        List<Move> legalMoves = MoveGenerator.generateLegalMoves(boardState);
        MoveOrdering.orderMoves(legalMoves);

        for (Move move : legalMoves) {
            if (System.currentTimeMillis() > endTime) {
                isTimeUp = true;
                return 0;
            }

            boardState.makeMove(move);
            int score = -negamax(boardState, depth - 1, -beta, -alpha, endTime);
            boardState.unmakeMove();

            if (isTimeUp) break;

            if (score > maxScore) {
                maxScore = score;
            }

            if (score > alpha) {
                alpha = score;
            }

            // Beta cutoff
            if (alpha >= beta) {
                break;
            }
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

        if (standPat >= beta) return beta;     // Fail-hard beta cutoff
        if (standPat > alpha) alpha = standPat;

        // Generate and order all legal captures
        List<Move> captures = MoveGenerator.generateCaptures(boardState);
        MoveOrdering.orderMoves(captures);  // Not sure if this matters much for performance

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
}
