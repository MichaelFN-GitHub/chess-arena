package com.MichaelFN.chess.v5;

import com.MichaelFN.chess.v5.board.Bitboard;
import com.MichaelFN.chess.v5.board.Board;

public class Zobrist {

    private static final int NUM_SQUARES = 64;
    private static final int NUM_COLORS = 2;
    private static final int NUM_PIECE_TYPES = 7; // 0 unused: [1..6] = pawn..king
    private static final int NUM_CASTLING_STATES = 16;
    private static final int NUM_FILES = 8;

    private static final long[][][] PIECE_KEYS = new long[NUM_COLORS][NUM_PIECE_TYPES][NUM_SQUARES];
    private static final long[] CASTLING_KEYS = new long[NUM_CASTLING_STATES];
    private static final long[] EN_PASSANT_KEYS = new long[NUM_FILES];
    private static long SIDE_TO_MOVE_KEY;

    static {
        SplitMix64 rng = new SplitMix64(0xABCDEF1234567890L); // Strong seed
        for (int color = 0; color < NUM_COLORS; color++) {
            for (int piece = 1; piece < NUM_PIECE_TYPES; piece++) { // Skip 0
                for (int square = 0; square < NUM_SQUARES; square++) {
                    PIECE_KEYS[color][piece][square] = rng.next();
                }
            }
        }

        for (int i = 0; i < NUM_CASTLING_STATES; i++) {
            CASTLING_KEYS[i] = rng.next();
        }

        for (int i = 0; i < NUM_FILES; i++) {
            EN_PASSANT_KEYS[i] = rng.next();
        }

        SIDE_TO_MOVE_KEY = rng.next();
    }

    private static class SplitMix64 {
        private long seed;

        public SplitMix64(long seed) {
            this.seed = seed;
        }

        public long next() {
            long z = (seed += 0x9E3779B97F4A7C15L);
            z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
            z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
            return z ^ (z >>> 31);
        }
    }

    // Computes the full Zobrist hash for a board position
    public static long computeHash(Board board) {
        long hash = 0L;

        for (int color = 0; color < NUM_COLORS; color++) {
            for (int piece = 1; piece < NUM_PIECE_TYPES; piece++) {
                long bitboard = board.pieces[color][piece];
                while (bitboard != 0) {
                    int square = Long.numberOfTrailingZeros(bitboard);
                    hash ^= PIECE_KEYS[color][piece][square];
                    bitboard = Bitboard.clearLsb(bitboard);
                }
            }
        }

        hash ^= CASTLING_KEYS[board.castlingRights];

        if (board.enPassantSquare != -1) {
            int file = board.enPassantSquare % 8;
            hash ^= EN_PASSANT_KEYS[file];
        }

        if (board.playerToMove == Constants.BLACK) {
            hash ^= SIDE_TO_MOVE_KEY;
        }

        return hash;
    }

    // Incremental hash updates
    public static long removePiece(long hash, int color, int pieceType, int square) {
        return hash ^ PIECE_KEYS[color][pieceType][square];
    }

    public static long addPiece(long hash, int color, int pieceType, int square) {
        return hash ^ PIECE_KEYS[color][pieceType][square];
    }

    public static long updateCastlingRights(long hash, int oldRights, int newRights) {
        if (oldRights != newRights) {
            hash ^= CASTLING_KEYS[oldRights];
            hash ^= CASTLING_KEYS[newRights];
        }
        return hash;
    }

    public static long updateEnPassant(long hash, int oldSquare, int newSquare) {
        if (oldSquare != newSquare) {
            if (oldSquare != -1) {
                hash ^= EN_PASSANT_KEYS[oldSquare % 8];
            }
            if (newSquare != -1) {
                hash ^= EN_PASSANT_KEYS[newSquare % 8];
            }
        }
        return hash;
    }

    public static long toggleSideToMove(long hash) {
        return hash ^ SIDE_TO_MOVE_KEY;
    }
}
