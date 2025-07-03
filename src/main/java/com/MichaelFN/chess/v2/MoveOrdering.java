package com.MichaelFN.chess.v2;

import com.MichaelFN.chess.v1.Move;

import java.util.List;

import static com.MichaelFN.chess.v2.Evaluator.PIECE_VALUES;

public class MoveOrdering {

    private static final int PV_MOVE_SCORE = 1000000;
    private static final int TT_MOVE_SCORE = 900000;
    private static final int PROMOTION_SCORE = 800000;
    private static final int[][] MVV_LVA_TABLE = new int[6][6]; // MVV_LVA_TABLE[victim][attacker]
    static {
        for (int victim = 0; victim < 6; victim++) {
            for (int attacker = 0; attacker < 6; attacker++) {
                MVV_LVA_TABLE[victim][attacker] = PIECE_VALUES[victim] * 10 - PIECE_VALUES[attacker];
            }
        }
    }

    public static void orderMoves(List<Move> moves, Move pvMove, Move ttMove) {
        moves.sort((move1, move2) -> {
            int score1 = scoreMove(move1, pvMove, ttMove);
            int score2 = scoreMove(move2, pvMove, ttMove);
            return Integer.compare(score2, score1);
        });
    }

    private static int scoreMove(Move move, Move pvMove, Move ttMove) {
        int score = 0;

        if (move.equals(pvMove)) {
            score += PV_MOVE_SCORE;
        } else if (move.equals(ttMove)) {
            score += TT_MOVE_SCORE;
        } else {
            score += getMVVLVA_Score(move);
            score += move.isPromotion() ? PROMOTION_SCORE : 0;

            // killer moves, history heuristic, etc.
        }

        return score;
    }

    public static int getMVVLVA_Score(Move move) {
        if (!move.isCapture()) return 0;

        int victimIdx = move.getCapturedPiece().type().ordinal();
        int attackerIdx = move.getMovedPiece().type().ordinal();
        return MVV_LVA_TABLE[victimIdx][attackerIdx];
    }
}
