package com.MichaelFN.chess.v5;

import com.MichaelFN.chess.v5.board.Bitboard;
import com.MichaelFN.chess.v5.board.Board;

import java.util.Random;

import static com.MichaelFN.chess.v5.Constants.CASTLING_MASK;

public class Zobrist {
    private static final int NUM_SQUARES = 64;
    private static final int NUM_COLORS = 2;
    private static final int NUM_PIECE_TYPES = 7;
    private static final long[][][] PIECE_KEYS = new long[NUM_COLORS][NUM_PIECE_TYPES][NUM_SQUARES];
    private static final long[] CASTLING_KEYS = new long[16];
    private static final long[] EN_PASSANT_KEYS = new long[8];
    private static long SIDE_TO_MOVE_KEY;

    private static final Random random = new Random(0xABCDEF);
    static {
        for (int color = 0; color < NUM_COLORS; color++) {
            for (int piece = 1; piece < NUM_PIECE_TYPES; piece++) { // Skip piece type 0
                for (int square = 0; square < NUM_SQUARES; square++) {
                    PIECE_KEYS[color][piece][square] = randomLong();
                }
            }
        }
        for (int i = 0; i < CASTLING_KEYS.length; i++) {
            CASTLING_KEYS[i] = randomLong();
        }
        for (int i = 0; i < EN_PASSANT_KEYS.length; i++) {
            EN_PASSANT_KEYS[i] = randomLong();
        }
        SIDE_TO_MOVE_KEY = randomLong();
    }

    private static long randomLong() {
        return random.nextLong();
    }

    // Computes the Zobrist hash for the given board
    public static long computeHash(Board board) {
        long hash = 0L;

        // Pieces
        for (int color = 0; color < NUM_COLORS; color++) {
            for (int piece = 1; piece < NUM_PIECE_TYPES; piece++) { // Skip piece type 0
                long bb = board.pieces[color][piece];
                while (bb != 0) {
                    int square = Long.numberOfTrailingZeros(bb);
                    hash ^= PIECE_KEYS[color][piece][square];
                    bb = Bitboard.clearLsb(bb);
                }
            }
        }

        // Castling rights
        hash ^= CASTLING_KEYS[board.castlingRights & CASTLING_MASK];

        // En passant file
        if (board.enPassantSquare != -1) {
            int file = board.enPassantSquare % 8;
            hash ^= EN_PASSANT_KEYS[file];
        }

        // Side to move
        if (board.playerToMove == Constants.BLACK) {
            hash ^= SIDE_TO_MOVE_KEY;
        }

        return hash;
    }

    // Methods for updating the hash key incrementally
    public static long removePiece(long hash, int color, int pieceType, int square) {
        return hash ^ PIECE_KEYS[color][pieceType][square];
    }

    public static long addPiece(long hash, int color, int pieceType, int square) {
        return hash ^ PIECE_KEYS[color][pieceType][square];
    }

    public static long updateCastlingRights(long hash, int oldRights, int newRights) {
        if (oldRights != newRights) {
            hash ^= CASTLING_KEYS[oldRights & 0b1111];
            hash ^= CASTLING_KEYS[newRights & 0b1111];
        }
        return hash;
    }

    public static long updateEnPassant(long hash, int oldEnPassant, int newEnPassant) {
        if (oldEnPassant != newEnPassant) {
            if (oldEnPassant != -1) {
                hash ^= EN_PASSANT_KEYS[oldEnPassant % 8];
            }
            if (newEnPassant != -1) {
                hash ^= EN_PASSANT_KEYS[newEnPassant % 8];
            }
        }
        return hash;
    }

    public static long toggleSideToMove(long hash) {
        return hash ^ SIDE_TO_MOVE_KEY;
    }
}
