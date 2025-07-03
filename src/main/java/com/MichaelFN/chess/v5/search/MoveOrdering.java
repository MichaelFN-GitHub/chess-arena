package com.MichaelFN.chess.v5.search;

import com.MichaelFN.chess.common.PestoConstants;
import com.MichaelFN.chess.v5.Constants;
import com.MichaelFN.chess.v5.board.Board;
import com.MichaelFN.chess.v5.move.Move;

public class MoveOrdering {
    private static final int NULL_SCORE = -1000000;
    private static final int PV_MOVE_SCORE = 1000000;
    private static final int TT_MOVE_SCORE = 900000;
    private static final int PROMOTION_SCORE = 800000;
    private static final int[][] MVV_LVA_TABLE = new int[7][7]; // MVV_LVA_TABLE[victim][attacker]
    static {
        for (int victim = 1; victim < 7; victim++) {
            for (int attacker = 1; attacker < 7; attacker++) {
                MVV_LVA_TABLE[victim][attacker] = PestoConstants.MG_VALUE[victim - 1] * 10 - PestoConstants.MG_VALUE[attacker - 1];
            }
        }
    }

    public static void orderMoves(int moveCount, int[] moves, int pvMove, int ttMove, Board board) {
        int[] scores = new int[moveCount];

        for (int i = 0; i < moveCount; i++) {
            int move = moves[i];

            if (move == 0) {
                scores[i] = NULL_SCORE;
                continue;
            }

            if (move == pvMove) scores[i] = PV_MOVE_SCORE;
            else if (move == ttMove) scores[i] = TT_MOVE_SCORE;
            else if (Move.isPromotion(move)) scores[i] = PROMOTION_SCORE;
            else scores[i] = getMVVLVA_Score(move, board);
        }

        quicksort(moves, scores, 0, moveCount - 1);
    }

    private static int getMVVLVA_Score(int move, Board board) {
        if (!Move.isCapture(move)) return 0;
        if (Move.isEnPassant(move)) return MVV_LVA_TABLE[Constants.PAWN][Constants.PAWN];

        int victim = board.pieceAtSquare[Move.getTo(move)];
        int attacker = board.pieceAtSquare[Move.getFrom(move)];

        return MVV_LVA_TABLE[victim][attacker];
    }

    private static void quicksort(int[] moves, int[] scores, int low, int high) {
        if (low < high) {
            int p = partition(moves, scores, low, high);
            quicksort(moves, scores, low, p - 1);
            quicksort(moves, scores, p + 1, high);
        }
    }

    private static int partition(int[] moves, int[] scores, int low, int high) {
        int pivot = scores[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (scores[j] > pivot) { // descending order
                i++;
                swap(moves, scores, i, j);
            }
        }
        swap(moves, scores, i + 1, high);
        return i + 1;
    }

    private static void swap(int[] moves, int[] scores, int i, int j) {
        int tmpMove = moves[i];
        moves[i] = moves[j];
        moves[j] = tmpMove;

        int tmpScore = scores[i];
        scores[i] = scores[j];
        scores[j] = tmpScore;
    }
}
