package com.MichaelFN.chess.v5;

public class Utils {

    public static int charToPieceType(char c) {
        return switch (Character.toLowerCase(c)) {
            case 'p' -> Constants.PAWN;
            case 'n' -> Constants.KNIGHT;
            case 'b' -> Constants.BISHOP;
            case 'r' -> Constants.ROOK;
            case 'q' -> Constants.QUEEN;
            case 'k' -> Constants.KING;
            default -> throw new IllegalArgumentException("Invalid piece char: " + c);
        };
    }

    public static int algebraicToSquare(String sq) {
        int file = sq.charAt(0) - 'a';
        int rank = sq.charAt(1) - '1';
        return rank * 8 + file;
    }
}
