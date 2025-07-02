package com.MichaelFN.chess.v5;

public class Constants {
    public static final int MAX_MOVES = 256;
    public static final int MAX_MOVES_IN_POSITION = 256;
    public static final int MAX_PLY = 64;

    // COLOR
    public static final int WHITE = 0;
    public static final int BLACK = 1;

    // PIECE TYPE
    public static final int ALL_PIECES = 0;
    public static final int NONE = 0;
    public static final int PAWN = 1;
    public static final int KNIGHT = 2;
    public static final int BISHOP = 3;
    public static final int ROOK = 4;
    public static final int QUEEN = 5;
    public static final int KING = 6;

    // GAME STATUS
    public static final int ONGOING = 0;
    public static final int WHITE_WIN = 1;
    public static final int BLACK_WIN = 2;
    public static final int DRAW = 3;

    // CASTLING
    public static final int CASTLE_WHITE_KINGSIDE = 0b1000;
    public static final int CASTLE_WHITE_QUEENSIDE = 0b0100;
    public static final int CASTLE_WHITE = CASTLE_WHITE_KINGSIDE | CASTLE_WHITE_QUEENSIDE;
    public static final int CASTLE_BLACK_KINGSIDE = 0b0010;
    public static final int CASTLE_BLACK_QUEENSIDE = 0b0001;
    public static final int CASTLE_BLACK = CASTLE_BLACK_KINGSIDE | CASTLE_BLACK_QUEENSIDE;
}
