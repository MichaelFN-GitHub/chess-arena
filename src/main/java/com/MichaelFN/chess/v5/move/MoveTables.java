package com.MichaelFN.chess.v5.move;

import static com.MichaelFN.chess.v5.board.Bitboard.*;
import static com.MichaelFN.chess.v5.Constants.*;

public class MoveTables {
    // Mask of pawn attacks from each square [color][square]
    public static long[][] PAWN_ATTACK_MASKS = new long[2][64];
    static {
        for (int i = 0; i < 64; i++) {
            long squareMask = SQUARE_BB_LOOK_UP[i];
            PAWN_ATTACK_MASKS[WHITE][i] = ((squareMask & ~FILE_A) << 7) | ((squareMask & ~FILE_H) << 9);
            PAWN_ATTACK_MASKS[BLACK][i] = ((squareMask & ~FILE_H) >>> 7) | ((squareMask & ~FILE_A) >>> 9);
        }
    }

    // Masks of knight moves for each square
    public static long[] KNIGHT_MOVE_MASKS = new long[64];

    static {
        for (int square = 0; square < 64; square++) {
            long mask = 0L;
            long knightBB = SQUARE_BB_LOOK_UP[square];
            mask |= ((knightBB << 17) & ~FILE_A);
            mask |= ((knightBB << 10) & ~FILE_AB);
            mask |= ((knightBB >>> 6) & ~FILE_AB);
            mask |= ((knightBB >>> 15) & ~FILE_A);
            mask |= ((knightBB << 15) & ~FILE_H);
            mask |= ((knightBB << 6) & ~FILE_GH);
            mask |= ((knightBB >>> 10) & ~FILE_GH);
            mask |= ((knightBB >>> 17) & ~FILE_H);
            KNIGHT_MOVE_MASKS[square] = mask;
        }
    }

    // Masks of king moves for each square
    public static long[] KING_MOVE_MASKS = new long[64];

    static {
        for (int square = 0; square < 64; square++) {
            long mask = 0L;
            long kingBB = SQUARE_BB_LOOK_UP[square];
            mask |= (kingBB << 1) & ~FILE_A;
            mask |= (kingBB << 7) & ~FILE_H & ~RANK_1;
            mask |= (kingBB << 8) & ~RANK_1;
            mask |= (kingBB << 9) & ~FILE_A & ~RANK_1;
            mask |= (kingBB >>> 1) & ~FILE_H;
            mask |= (kingBB >>> 7) & ~FILE_A & ~RANK_8;
            mask |= (kingBB >>> 8) & ~RANK_8;
            mask |= (kingBB >>> 9) & ~FILE_H & ~RANK_8;
            KING_MOVE_MASKS[square] = mask;
        }
    }
}