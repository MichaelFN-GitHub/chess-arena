package com.MichaelFN.chess.v5.move;

import static com.MichaelFN.chess.v5.Utils.PIECE_NAMES;
import static com.MichaelFN.chess.v5.Utils.SQUARE_NAMES;

public class Move {
    /**
     * Encodes a chess move into a single int using bit fields:
     *
     * | Bits  | Field         | Description                           |
     * |-------|---------------|---------------------------------------|
     * | 0–5   | FROM          | Source square (0–63)                  |
     * | 6–11  | TO            | Destination square (0–63)             |
     * | 12–19 | FLAGS         | Move flags (capture, promotion, etc.) |
     * | 20–23 | PROMO TYPE    | Promotion piece type (if any)         |
     * | 24-27 | CAPTURED TYPE | Captured piece type (if any)          |
     *
     * Includes factory methods to create moves and extractors to decode them.
     */

    // Bit shifts
    public static final int FROM_SHIFT = 0;
    public static final int TO_SHIFT = 6;
    public static final int FLAG_SHIFT = 12;
    public static final int PROMO_SHIFT = 20;
    public static final int CAPTURED_SHIFT = 24;

    // Masks
    public static final int SQUARE_MASK = 0b111111;    // 6 bits
    public static final int FLAG_MASK = 0b11111111;    // 8 bits
    public static final int PIECE_MASK = 0b1111;       // 4 bits

    // Flags
    public static final int FLAG_CAPTURE           = 1 << 0;
    public static final int FLAG_DOUBLE_PAWN_PUSH  = 1 << 1;
    public static final int FLAG_KINGSIDE_CASTLE   = 1 << 2;
    public static final int FLAG_QUEENSIDE_CASTLE  = 1 << 3;
    public static final int FLAG_EN_PASSANT        = 1 << 4;
    public static final int FLAG_PROMOTION         = 1 << 5;

    // Factory methods
    public static int createQuietMove(int from, int to) {
        return encodeMove(from, to, 0, 0);
    }

    public static int createCapture(int from, int to) {
        return encodeMove(from, to, FLAG_CAPTURE, 0);
    }

    public static int createDoublePawnPush(int from, int to) {
        return encodeMove(from, to, FLAG_DOUBLE_PAWN_PUSH, 0);
    }

    public static int createCastleKingSide(int from, int to) {
        return encodeMove(from, to, FLAG_KINGSIDE_CASTLE, 0);
    }

    public static int createCastleQueenSide(int from, int to) {
        return encodeMove(from, to, FLAG_QUEENSIDE_CASTLE, 0);
    }

    public static int createEnPassantCapture(int from, int to) {
        return encodeMove(from, to, FLAG_EN_PASSANT | FLAG_CAPTURE, 0);
    }

    public static int createPromotionMove(int from, int to, int promoPiece) {
        return encodeMove(from, to, FLAG_PROMOTION, promoPiece);
    }

    public static int createPromotionCapture(int from, int to, int promoPiece) {
        return encodeMove(from, to, FLAG_PROMOTION | FLAG_CAPTURE, promoPiece);
    }

    // Encoding
    private static int encodeMove(int from, int to, int flags, int promo) {
        return ((from & SQUARE_MASK) << FROM_SHIFT) |
                ((to & SQUARE_MASK) << TO_SHIFT) |
                ((flags & FLAG_MASK) << FLAG_SHIFT) |
                ((promo & PIECE_MASK) << PROMO_SHIFT);
    }

    // Extractors
    public static int getFrom(int move) {
        return (move >> FROM_SHIFT) & SQUARE_MASK;
    }

    public static int getTo(int move) {
        return (move >> TO_SHIFT) & SQUARE_MASK;
    }

    public static int getFlags(int move) {
        return (move >> FLAG_SHIFT) & FLAG_MASK;
    }

    public static int getPromotionPiece(int move) {
        return (move >> PROMO_SHIFT) & PIECE_MASK;
    }

    public static int getCapturedPiece(int move) {
        return (move >> CAPTURED_SHIFT) & PIECE_MASK;
    }

    // Checkers
    public static boolean isCapture(int move) {
        return (getFlags(move) & FLAG_CAPTURE) != 0;
    }

    public static boolean isPromotion(int move) {
        return (getFlags(move) & FLAG_PROMOTION) != 0;
    }

    public static boolean isDoublePawnPush(int move) {
        return (getFlags(move) & FLAG_DOUBLE_PAWN_PUSH) != 0;
    }

    public static boolean isCastleKingSide(int move) {
        return (getFlags(move) & FLAG_KINGSIDE_CASTLE) != 0;
    }

    public static boolean isCastleQueenSide(int move) {
        return (getFlags(move) & FLAG_QUEENSIDE_CASTLE) != 0;
    }

    public static boolean isEnPassant(int move) {
        return (getFlags(move) & FLAG_EN_PASSANT) != 0;
    }

    public static boolean isQuiet(int move) {
        return getFlags(move) == 0;
    }

    public static String toString(int move) {
        String from = SQUARE_NAMES[getFrom(move)];
        String to = SQUARE_NAMES[getTo(move)];
        StringBuilder sb = new StringBuilder(from).append(to);

        if (isCastleKingSide(move)) {
            sb = new StringBuilder(from + to + " (O-O)");
        } else if (isCastleQueenSide(move)) {
            sb = new StringBuilder(from + to + " (O-O-O)");
        } else {
            if (isPromotion(move)) {
                sb.append("=").append(PIECE_NAMES[getPromotionPiece(move)]);
            }
            if (isCapture(move)) {
                sb.append(" (capture)");
            }
            if (isEnPassant(move)) {
                sb.append(" (en passant)");
            }
            if (isDoublePawnPush(move)) {
                sb.append(" (double pawn push)");
            }
        }
        return sb.toString();
    }
}
