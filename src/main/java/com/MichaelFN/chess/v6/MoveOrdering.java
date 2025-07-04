package com.MichaelFN.chess.v6;

import com.MichaelFN.chess.common.PestoConstants;
import com.MichaelFN.chess.v5.board.Board;
import com.MichaelFN.chess.v5.move.Move;

public class MoveOrdering {
    private static final int NULL_SCORE = -1000000;
    private static final int PV_MOVE_SCORE = 1000000;
    private static final int TT_MOVE_SCORE = 900000;
    private static final int KILLER_MOVE_SCORE = 800000;
    private static final int PROMOTION_SCORE = 700000;
    private static final int EN_PASSANT_SCORE = 10000;
    private static final int HISTORY_BONUS = 1000;

    private static final int[][] MVV_LVA_TABLE = new int[7][7]; // MVV_LVA_TABLE[victim][attacker]
    static {
        for (int victim = 1; victim < 7; victim++) {
            for (int attacker = 1; attacker < 7; attacker++) {
                MVV_LVA_TABLE[victim][attacker] = PestoConstants.MG_VALUE[victim - 1] * 10 - PestoConstants.MG_VALUE[attacker - 1];
            }
        }
    }

    public static void orderMoves(int moveCount, int[] moves, int pvMove, int[][] killerMoves, int ttMove, int[][] historyHeuristic, Board board, int ply) {
        int[] scores = new int[moveCount];

        boolean checkKillerMoves = killerMoves != null;
        boolean checkHistoryHeuristic = historyHeuristic != null;

        for (int i = 0; i < moveCount; i++) {
            int move = moves[i];

            if (move == 0) {
                scores[i] = NULL_SCORE;
                continue;
            }

            if (move == pvMove) {
                scores[i] = PV_MOVE_SCORE;
            } else if (move == ttMove) {
                scores[i] = TT_MOVE_SCORE;
            } else if (checkKillerMoves && (move == killerMoves[ply][0] || move == killerMoves[ply][1])) {
                scores[i] = KILLER_MOVE_SCORE;
            } else if (checkHistoryHeuristic && !Move.isCapture(move) && !Move.isPromotion(move)) {
                int piece = board.pieceAtSquare[Move.getFrom(move)];
                int to = Move.getTo(move);
                if (piece > 0) {
                    scores[i] = historyHeuristic[piece][to] + HISTORY_BONUS;
                } else {
                    scores[i] = 0;
                }
            } else if (Move.isPromotion(move)) {
                scores[i] = PROMOTION_SCORE;
            } else {
                scores[i] = getMVVLVA_Score(move, board);
            }
        }

        insertionSort(moves, scores, moveCount);
    }

    private static int getMVVLVA_Score(int move, Board board) {
        if (!Move.isCapture(move)) return 0;
        if (Move.isEnPassant(move)) return EN_PASSANT_SCORE;

        int victim = board.pieceAtSquare[Move.getTo(move)];
        int attacker = board.pieceAtSquare[Move.getFrom(move)];

        return MVV_LVA_TABLE[victim][attacker];
    }

    private static void insertionSort(int[] moves, int[] scores, int length) {
        for (int i = 1; i < length; i++) {
            int move = moves[i];
            int score = scores[i];
            int j = i - 1;

            // Sort descending order
            while (j >= 0 && scores[j] < score) {
                moves[j + 1] = moves[j];
                scores[j + 1] = scores[j];
                j--;
            }
            moves[j + 1] = move;
            scores[j + 1] = score;
        }
    }
}
