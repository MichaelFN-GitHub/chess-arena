package com.MichaelFN.chess.V2;

import com.MichaelFN.chess.V1.BoardState;
import com.MichaelFN.chess.V1.GameStatus;
import com.MichaelFN.chess.V1.Move;
import com.MichaelFN.chess.V1.MoveGenerator;

import java.util.List;

public class Negamax {
    private final Evaluator evaluator;

    private int nodesSearched;
    private boolean isTimeUp;

    public Negamax(Evaluator evaluator) {
        this.evaluator = evaluator;
    }

    public Move findBestMove(BoardState boardState, int maxDepth, long time) {
        isTimeUp = false;
        Move bestMove = null;

        long startTime = System.currentTimeMillis();
        long endTime = startTime + time;

        List<Move> legalMoves = MoveGenerator.generateLegalMoves(boardState);
        MoveOrdering.orderMoves(legalMoves);

        for (int depth = 1; depth <= maxDepth; depth++) {
            int bestScoreThisDepth = Integer.MIN_VALUE;
            Move bestMoveThisDepth = null;

            int alpha = Integer.MIN_VALUE + 1;
            int beta = Integer.MAX_VALUE - 1;

            for (Move move : legalMoves) {
                boardState.makeMove(move);
                int score = -negamax(boardState, depth, alpha, beta, endTime);
                boardState.unmakeMove();

                if (isTimeUp) break;

                if (score > bestScoreThisDepth) {
                    bestScoreThisDepth = score;
                    bestMoveThisDepth = move;
                }
            }

            if (isTimeUp) break;
            bestMove = bestMoveThisDepth;
            System.out.println("Depth " + depth + " searched. Current best move + bestMove");
        }

        System.out.println("Nodes searched: " + nodesSearched);
        System.out.println("Time used: " + (System.currentTimeMillis() - startTime));
        return bestMove;
    }

    private int negamax(BoardState boardState, int depth, int alpha, int beta, long endTime) {
        if (System.currentTimeMillis() > endTime) {
            isTimeUp = true;
            return 0;
        }

        nodesSearched++;

        GameStatus gameStatus = GameStatus.evaluateGameStatus(boardState);
        if (gameStatus.isGameOver()) {
            if (gameStatus.isCheckmate()) return -9999999 - depth;

            // Stalemate
            return 0;
        }

        if (depth == 0) {
            return quiescence(boardState, alpha, beta, endTime);
        }

        int maxScore = Integer.MIN_VALUE;
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

        int standPat = evaluator.evaluate(boardState);

        if (standPat >= beta) return beta;
        if (standPat > alpha) alpha = standPat;

        List<Move> captures = MoveGenerator.generateCaptures(boardState);
        MoveOrdering.orderMoves(captures);

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
