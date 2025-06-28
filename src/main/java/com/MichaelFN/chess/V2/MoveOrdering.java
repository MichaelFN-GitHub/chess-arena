package com.MichaelFN.chess.V2;

import com.MichaelFN.chess.V1.Move;

import java.util.List;

public class MoveOrdering {
    public static void orderMoves(List<Move> moves) {
        moves.sort((move1, move2) -> {
            int score1 = MoveOrdering.getMVVLVA_Score(move1);
            int score2 = MoveOrdering.getMVVLVA_Score(move2);
            return Integer.compare(score2, score1);
        });
    }

    public static int getMVVLVA_Score(Move move) {
        if (!move.isCapture()) return 0;

        int victimValue = Evaluator.PIECE_VALUES[move.getCapturedPiece().type().ordinal()];
        int attackerValue = Evaluator.PIECE_VALUES[move.getMovedPiece().type().ordinal()];

        return victimValue * 10 - attackerValue;
    }
}
