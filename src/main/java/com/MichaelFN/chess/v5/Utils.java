package com.MichaelFN.chess.v5;

import com.MichaelFN.chess.v5.board.Bitboard;
import com.MichaelFN.chess.v5.board.Board;

import static com.MichaelFN.chess.v5.Constants.*;
import static com.MichaelFN.chess.v5.board.Bitboard.*;
import static com.MichaelFN.chess.v5.move.MoveTables.*;

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
            case 'p' -> PAWN;
            case 'n' -> Constants.KNIGHT;
            case 'b' -> Constants.BISHOP;
            case 'r' -> Constants.ROOK;
            case 'q' -> Constants.QUEEN;
            case 'k' -> Constants.KING;
            default -> throw new IllegalArgumentException("Invalid piece char: " + c);
        };
    }

    public static char pieceTypeToChar(int pieceType, int color) {
        char c = switch (pieceType) {
            case 1 -> 'P';
            case 2 -> 'N';
            case 3 -> 'B';
            case 4 -> 'R';
            case 5 -> 'Q';
            case 6 -> 'K';
            default -> '.';
        };
        return color == 0 ? c : Character.toLowerCase(c);
    }

    public static int algebraicToSquare(String sq) {
        int file = sq.charAt(0) - 'a';
        int rank = sq.charAt(1) - '1';
        return rank * 8 + file;
    }

    public static String squareToAlgebraic(int square) {
        int file = square % 8;
        int rank = square / 8;
        return "" + (char) ('a' + file) + (rank + 1);
    }


    public static boolean isSquareAttacked(Board board, int square, int byColor, long occupancy) {
        long[] byPieces = board.pieces[byColor];

        // PAWN attacks
        long pawnAttacks = PAWN_ATTACK_MASKS[1 - byColor][square];
        if ((byPieces[PAWN] & pawnAttacks) != 0) return true;

        // KNIGHT attacks
        if ((byPieces[KNIGHT] & KNIGHT_MOVE_MASKS[square]) != 0) return true;

        // KING attacks
        if ((byPieces[KING] & KING_MOVE_MASKS[square]) != 0) return true;

        // BISHOP + QUEEN diagonal attacks
        long bishopLikeAttackers = byPieces[BISHOP] | byPieces[QUEEN];
        if ((getBishopMoves(square, occupancy) & bishopLikeAttackers) != 0) return true;

        // ROOK + QUEEN straight attacks
        long rookLikeAttackers = byPieces[ROOK] | byPieces[QUEEN];
        if ((getRookMoves(square, occupancy) & rookLikeAttackers) != 0) return true;

        return false;
    }

    public static boolean isInCheck(Board board, int color) {
        int enemyColor = 1 - color;

        long kingBB = board.pieces[color][Constants.KING];
        int kingSquare = Bitboard.lsb(kingBB);
        long occupancy = board.pieces[Constants.WHITE][Constants.ALL_PIECES] | board.pieces[Constants.BLACK][Constants.ALL_PIECES];

        return isSquareAttacked(board, kingSquare, enemyColor, occupancy);
    }

    public static long getBishopMoves(int fromSquare, long occupancy) {
        return hyperbolaQuintessence(fromSquare, DIAGONAL_MASKS_PER_SQUARE[fromSquare], occupancy) |
                hyperbolaQuintessence(fromSquare, ANTI_DIAGONAL_MASKS_PER_SQUARE[fromSquare], occupancy);
    }

    public static long getRookMoves(int fromSquare, long occupancy) {
        return hyperbolaQuintessence(fromSquare, RANK_MASKS_PER_SQUARE[fromSquare], occupancy) |
                hyperbolaQuintessence(fromSquare, FILE_MASKS_PER_SQUARE[fromSquare], occupancy);
    }

    // Computes the sliding moves along a single line or diagonal
    public static long hyperbolaQuintessence(int square, long mask, long occupancy) {
        long occ = occupancy & mask;
        long squareBB = SQUARE_BB_LOOK_UP[square];
        long left = occ - 2*squareBB;
        long right = Long.reverse(Long.reverse(occ) - 2*Long.reverse(squareBB));
        return (left ^ right) & mask;
    }
}
