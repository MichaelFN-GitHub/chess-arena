package com.MichaelFN.chess.v2;

import com.MichaelFN.chess.v1.Move;

import java.util.List;

public class MoveOrdering {
    public static void orderMoves(List<Move> moves, Move pvMove) {
        moves.sort((move1, move2) -> {
            // If either move is the PV move, it goes first
            if (pvMove != null) {
                if (move1.equals(pvMove)) return -1;
                if (move2.equals(pvMove)) return 1;
            }

            // Otherwise, sort by MVV-LVA score descending
            int score1 = getMVVLVA_Score(move1);
            int score2 = getMVVLVA_Score(move2);
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
