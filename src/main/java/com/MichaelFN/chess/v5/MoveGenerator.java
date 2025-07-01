package com.MichaelFN.chess.v5;

import static com.MichaelFN.chess.v5.Bitboard.*;
import static com.MichaelFN.chess.v5.Constants.*;

public class MoveGenerator {
    public static int[] moves;
    public static int moveCounter;

    public static void generatePseudoLegalMoves() {
        moves = new int[MAX_MOVES_IN_POSITION];
        moveCounter = 0;
    }

    private static void addMove(int move) {
        moves[moveCounter++] = move;
    }
}
