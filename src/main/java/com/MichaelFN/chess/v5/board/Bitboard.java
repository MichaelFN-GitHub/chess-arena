package com.MichaelFN.chess.v5.board;

public final class Bitboard {

    public static final int A1 = 0;
    public static final int B1 = 1;
    public static final int C1 = 2;
    public static final int D1 = 3;
    public static final int E1 = 4;
    public static final int F1 = 5;
    public static final int G1 = 6;
    public static final int H1 = 7;

    public static final int A2 = 8;
    public static final int B2 = 9;
    public static final int C2 = 10;
    public static final int D2 = 11;
    public static final int E2 = 12;
    public static final int F2 = 13;
    public static final int G2 = 14;
    public static final int H2 = 15;

    public static final int A3 = 16;
    public static final int B3 = 17;
    public static final int C3 = 18;
    public static final int D3 = 19;
    public static final int E3 = 20;
    public static final int F3 = 21;
    public static final int G3 = 22;
    public static final int H3 = 23;

    public static final int A4 = 24;
    public static final int B4 = 25;
    public static final int C4 = 26;
    public static final int D4 = 27;
    public static final int E4 = 28;
    public static final int F4 = 29;
    public static final int G4 = 30;
    public static final int H4 = 31;

    public static final int A5 = 32;
    public static final int B5 = 33;
    public static final int C5 = 34;
    public static final int D5 = 35;
    public static final int E5 = 36;
    public static final int F5 = 37;
    public static final int G5 = 38;
    public static final int H5 = 39;

    public static final int A6 = 40;
    public static final int B6 = 41;
    public static final int C6 = 42;
    public static final int D6 = 43;
    public static final int E6 = 44;
    public static final int F6 = 45;
    public static final int G6 = 46;
    public static final int H6 = 47;

    public static final int A7 = 48;
    public static final int B7 = 49;
    public static final int C7 = 50;
    public static final int D7 = 51;
    public static final int E7 = 52;
    public static final int F7 = 53;
    public static final int G7 = 54;
    public static final int H7 = 55;

    public static final int A8 = 56;
    public static final int B8 = 57;
    public static final int C8 = 58;
    public static final int D8 = 59;
    public static final int E8 = 60;
    public static final int F8 = 61;
    public static final int G8 = 62;
    public static final int H8 = 63;

    public static final long FILE_A = 0x0101010101010101L;
    public static final long FILE_B = FILE_A << 1;
    public static final long FILE_C = FILE_A << 2;
    public static final long FILE_D = FILE_A << 3;
    public static final long FILE_E = FILE_A << 4;
    public static final long FILE_F = FILE_A << 5;
    public static final long FILE_G = FILE_A << 6;
    public static final long FILE_H = FILE_A << 7;
    public static final long FILE_AB = FILE_A | FILE_B;
    public static final long FILE_GH = FILE_G | FILE_H;

    public static final long RANK_1 = 0x00000000000000FFL;
    public static final long RANK_2 = RANK_1 << 8;
    public static final long RANK_3 = RANK_1 << 16;
    public static final long RANK_4 = RANK_1 << 24;
    public static final long RANK_5 = RANK_1 << 32;
    public static final long RANK_6 = RANK_1 << 40;
    public static final long RANK_7 = RANK_1 << 48;
    public static final long RANK_8 = RANK_1 << 56;
    public static final long RANK_1234567 = ~RANK_8;
    public static final long RANK_2345678 = ~RANK_1;

    public static final long DARK_SQUARES = 0x55AA55AA55AA55AAL;
    public static final long LIGHT_SQUARES = ~DARK_SQUARES;

    // Look-up table for single bit bitboards for each square on the chess board
    public static long[] SQUARE_BB_LOOK_UP = new long[64];
    static {
        for (int i = 0; i < 64; i++) {
            SQUARE_BB_LOOK_UP[i] = setBit(0L, i);
        }
    }

    // Masks of ranks and files
    public static final long[] RANK_MASKS = new long[8];
    public static final long[] FILE_MASKS = new long[8];
    static {
        for (int rank = 0; rank < 8; rank++) {
            long mask = 0L;
            for (int file = 0; file < 8; file++) {
                mask |= (1L << (rank * 8 + file));
            }
            RANK_MASKS[rank] = mask;
        }
    }

    public static final long[] RANK_MASKS_PER_SQUARE = new long[64];
    public static final long[] FILE_MASKS_PER_SQUARE = new long[64];
    static {
        for (int file = 0; file < 8; file++) {
            long mask = 0L;
            for (int rank = 0; rank < 8; rank++) {
                mask |= (1L << (rank * 8 + file));
            }
            FILE_MASKS[file] = mask;
        }

        for (int square = 0; square < 64; square++) {
            int rank = square / 8;
            int file = square % 8;

            RANK_MASKS_PER_SQUARE[square] = RANK_MASKS[rank];
            FILE_MASKS_PER_SQUARE[square] = FILE_MASKS[file];
        }
    }


    // Masks of diagonals and anti-diagonals
    public static long[] DIAGONAL_MASKS = new long[15];
    public static long[] ANTI_DIAGONAL_MASKS = new long[15];
    static {
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                int diagonalIndex = rank + file;
                int antiDiagonalIndex = rank + 7 - file;

                DIAGONAL_MASKS[diagonalIndex] |= SQUARE_BB_LOOK_UP[rank * 8 + file];
                ANTI_DIAGONAL_MASKS[antiDiagonalIndex] |= SQUARE_BB_LOOK_UP[rank * 8 + file];
            }
        }
    }

    public static final long[] DIAGONAL_MASKS_PER_SQUARE = new long[64];
    public static final long[] ANTI_DIAGONAL_MASKS_PER_SQUARE = new long[64];
    static {
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                int square = rank * 8 + file;
                int diagonalIndex = rank + file;
                int antiDiagonalIndex = rank + 7 - file;

                DIAGONAL_MASKS_PER_SQUARE[square] = DIAGONAL_MASKS[diagonalIndex];
                ANTI_DIAGONAL_MASKS_PER_SQUARE[square] = ANTI_DIAGONAL_MASKS[antiDiagonalIndex];
            }
        }
    }

    public static boolean hasBit(long bb, int i) {
        return (bb & (1L << i)) != 0;
    }

    public static int getBit(long bb, int i) {
        return (int)((bb >> i) & 1);
    }

    public static long setBit(long bb, int i) {
        return bb | (1L << i);
    }

    public static int bitCount(long bb) {
        return Long.bitCount(bb);
    }

    public static int lsb(long bb) {
        return Long.numberOfTrailingZeros(bb);
    }

    public static int msb(long bb) {
        return 63 - Long.numberOfLeadingZeros(bb);
    }

    public static long clearLsb(long bb) {
        return bb & (bb - 1);
    }

    public static int mirror(int square) {
        return square ^ 56;
    }

    public static boolean isEmpty(long bb) {
        return bb == 0L;
    }

    public static long squareToBitboard(int sq) {
        return SQUARE_BB_LOOK_UP[sq];
    }

    public static String toString(long bb) {
        StringBuilder sb = new StringBuilder();
        for (int rank = 7; rank >= 0; rank--) {
            sb.append(rank + 1).append("  ");
            for (int file = 0; file < 8; file++) {
                int sq = rank * 8 + file;
                sb.append((bb & (1L << sq)) != 0 ? "1 " : ". ");
            }
            sb.append('\n');
        }
        sb.append("\n   a b c d e f g h\n");
        return sb.toString();
    }

    public static void printBitboard(long bb) {
        System.out.println(toString(bb));
    }
}

