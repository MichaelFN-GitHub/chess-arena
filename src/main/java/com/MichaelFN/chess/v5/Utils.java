package com.MichaelFN.chess.v5;

import com.MichaelFN.chess.v5.board.Bitboard;
import com.MichaelFN.chess.v5.board.Board;
import com.MichaelFN.chess.v5.move.Move;

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
            case 'p' -> Constants.PAWN;
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

    public static String moveToUci(int move, Board board) {
        int from = Move.getFrom(move);
        int to = Move.getTo(move);

        String fromSq = SQUARE_NAMES[from];
        String toSq = SQUARE_NAMES[to];

        StringBuilder sb = new StringBuilder(fromSq).append(toSq);

        if (Move.isPromotion(move)) {
            int promo = Move.getPromotionPiece(move);
            char promoChar = pieceTypeToChar(promo, board.playerToMove);
            sb.append(promoChar);
        }

        return sb.toString();
    }

    public static int uciToMove(String uci, Board board) {
        int from = algebraicToSquare(uci.substring(0, 2));
        int to = algebraicToSquare(uci.substring(2, 4));

        int flags = 0;
        int promo = 0;

        // Check promotion
        if (uci.length() == 5) {
            flags |= Move.FLAG_PROMOTION;
            char promoChar = uci.charAt(4);
            promo = charToPieceType(promoChar);
        }

        // Identify the moving piece type from board
        int movingPiece = board.pieceAtSquare[from];

        // Check capture by seeing if opponent piece exists on `to` square
        boolean isCapture = board.pieceAtSquare[to] != 0;

        if (isCapture) {
            flags |= Move.FLAG_CAPTURE;
        }

        // Check special moves:
        // 1) Castling: from e1/e8 to g1/c1 or g8/c8
        if (movingPiece == KING) {
            if (from == algebraicToSquare("e1") && to == algebraicToSquare("g1") && board.playerToMove == WHITE) {
                flags = Move.FLAG_KINGSIDE_CASTLE;
                promo = 0;
                isCapture = false;
            } else if (from == algebraicToSquare("e1") && to == algebraicToSquare("c1") && board.playerToMove == WHITE) {
                flags = Move.FLAG_QUEENSIDE_CASTLE;
                promo = 0;
                isCapture = false;
            } else if (from == algebraicToSquare("e8") && to == algebraicToSquare("g8") && board.playerToMove == BLACK) {
                flags = Move.FLAG_KINGSIDE_CASTLE;
                promo = 0;
                isCapture = false;
            } else if (from == algebraicToSquare("e8") && to == algebraicToSquare("c8") && board.playerToMove == BLACK) {
                flags = Move.FLAG_QUEENSIDE_CASTLE;
                promo = 0;
                isCapture = false;
            }
        }

        // 2) En passant capture: if move matches en passant square
        if ((flags & Move.FLAG_CAPTURE) == 0 // only if not already normal capture
                && to == board.enPassantSquare
                && movingPiece == PAWN) {
            flags |= Move.FLAG_EN_PASSANT | Move.FLAG_CAPTURE;
        }

        // 3) Double pawn push (two squares forward)
        if (movingPiece == PAWN) {
            int fromRank = from / 8;
            int toRank = to / 8;
            if (board.playerToMove == WHITE && (toRank - fromRank) == 2) {
                flags |= Move.FLAG_DOUBLE_PAWN_PUSH;
            } else if (board.playerToMove == BLACK && (fromRank - toRank) == 2) {
                flags |= Move.FLAG_DOUBLE_PAWN_PUSH;
            }
        }

        // Create the move
        if ((flags & Move.FLAG_PROMOTION) != 0 && (flags & Move.FLAG_CAPTURE) != 0) {
            return Move.createPromotionCapture(from, to, promo);
        } else if ((flags & Move.FLAG_PROMOTION) != 0) {
            return Move.createPromotionMove(from, to, promo);
        } else if ((flags & Move.FLAG_CAPTURE) != 0) {
            return Move.createCapture(from, to);
        } else if ((flags & Move.FLAG_DOUBLE_PAWN_PUSH) != 0) {
            return Move.createDoublePawnPush(from, to);
        } else if ((flags & Move.FLAG_KINGSIDE_CASTLE) != 0) {
            return Move.createCastleKingSide(from, to);
        } else if ((flags & Move.FLAG_QUEENSIDE_CASTLE) != 0) {
            return Move.createCastleQueenSide(from, to);
        } else if ((flags & Move.FLAG_EN_PASSANT) != 0) {
            return Move.createEnPassantCapture(from, to);
        } else {
            return Move.createQuietMove(from, to);
        }
    }
}
