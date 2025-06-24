package com.MichaelFN.chess.V1;

public class MoveGenerator {
    public static void generatePawnMoves(Piece pawn, int rank, int file, Piece[][] position) {
        int forward = pawn.getColor() == Color.WHITE ? 1 : -1;

        // Push one square
        int toRank = rank + forward;

    }
}
