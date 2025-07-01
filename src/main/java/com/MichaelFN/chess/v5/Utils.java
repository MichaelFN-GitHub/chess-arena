package com.MichaelFN.chess.v5;

public class Utils {

    public static final String[] SQUARE_NAMES = {
            "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1",
            "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
            "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
            "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
            "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
            "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
            "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
            "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8",
    };

    public static final String[] PIECE_NAMES = {
            " ", "P", "N", "B", "R", "Q", "K"
    };

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
