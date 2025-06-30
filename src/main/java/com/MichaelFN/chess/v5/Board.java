package com.MichaelFN.chess.v5;

import java.util.Arrays;
import java.util.HashMap;

import static com.MichaelFN.chess.v5.Constants.MAX_MOVES;

public class Board {
    /**
     * Bitboards are an efficient representation of a chessboard.
     * Bitboards are 64-bit longs where each bit corresponds to a square on the board.
     * In my case, bit index 0 (LSB) represents a1 and bit index 63 (MSB) represents h8.
     * This is called Little-Endian Rank-File Mapping.
     *
     * Each bitboard encodes the presence of a specific type of piece or a group of pieces.
     * For example, one bitboard may represent all white pawns.
     *
     * Read more: <a href="https://www.chessprogramming.org/Bitboards">...</a>
     */

    // Chess rule variables
    public long[][] pieces;     // Bitboards: pieces[color][pieceType], where last type is all pieces
    public int playerToMove;    // 0 = white, 1 = black
    public int castlingRights;  // 1000 = white k-side, 0100 = white q-side, 0010 = black k-side, 0001 = black q-side
    public int enPassantSquare; // 0..63 -> a1..h8
    public int halfmoveClock;   // 0..50
    public int fullmoveNumber;  // 1..MAX_MOVES
    public int gameStatus;      // 0 = ongoing, 1 = white win, 2 = black win, 3 = draw
    public long hashKey;
    public final HashMap<Long,Integer> repetitionCount = new HashMap<>();

    // History variables
    private final int[] moveHistory = new int[MAX_MOVES];
    private final int[] castlingRightsHistory = new int[MAX_MOVES];
    private final int[] enPassantSquareHistory = new int[MAX_MOVES];
    private final int[] halfmoveClockHistory = new int[MAX_MOVES];
    private final int[] fullmoveNumberHistory = new int[MAX_MOVES];
    private final long[] hashKeyHistory = new long[MAX_MOVES];


    public Board() {
        this.pieces = new long[2][7];
        BoardInitializer.initializeBoard(this);
    }

    public void clear() {
        pieces = new long[2][7];
        repetitionCount.clear();
        Arrays.fill(moveHistory, 0);
        Arrays.fill(castlingRightsHistory, 0);
        Arrays.fill(enPassantSquareHistory, 0);
        Arrays.fill(halfmoveClockHistory, 0);
        Arrays.fill(fullmoveNumberHistory, 0);
        Arrays.fill(hashKeyHistory, 0L);
    }
}
