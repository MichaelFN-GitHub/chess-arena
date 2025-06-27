package com.MichaelFN.chess.V1;

import java.util.HashMap;
import java.util.Map;

public class Utils {
    public static final int[][] KNIGHT_JUMPS = {{-2,-1}, {-2,1}, {-1,-2}, {-1,2}, {1,-2}, {1,2}, {2,-1}, {2,1}};
    public static final int[][] KING_JUMPS = {{1,0}, {-1,0}, {0,1}, {0,-1}, {1,1}, {1,-1}, {-1,1}, {-1,-1}};
    public static final int[][] BISHOP_DIRECTIONS = {{1,1}, {1,-1}, {-1,1}, {-1,-1}};
    public static final int[][] ROOK_DIRECTIONS = {{1,0}, {-1,0}, {0,1}, {0,-1}};

    public static String pieceToString(Piece piece) {
        if (piece == null) return " ";
        return switch (piece.getType()) {
            case PAWN -> piece.getColor() == Color.WHITE ? "P" : "p";
            case KNIGHT -> piece.getColor() == Color.WHITE ? "N" : "n";
            case BISHOP -> piece.getColor() == Color.WHITE ? "B" : "b";
            case ROOK -> piece.getColor() == Color.WHITE ? "R" : "r";
            case QUEEN -> piece.getColor() == Color.WHITE ? "Q" : "q";
            case KING -> piece.getColor() == Color.WHITE ? "K" : "k";
        };
    }

    public static Piece stringToPiece(String s) {
        if (s == null || s.isEmpty() || s.equals(" ")) {
            return null;
        }

        char c = s.charAt(0);
        Color color = Character.isUpperCase(c) ? Color.WHITE : Color.BLACK;

        PieceType type;
        switch (Character.toUpperCase(c)) {
            case 'P' -> type = PieceType.PAWN;
            case 'N' -> type = PieceType.KNIGHT;
            case 'B' -> type = PieceType.BISHOP;
            case 'R' -> type = PieceType.ROOK;
            case 'Q' -> type = PieceType.QUEEN;
            case 'K' -> type = PieceType.KING;
            default -> throw new IllegalArgumentException("Invalid piece character: " + c);
        }

        return new Piece(type, color);
    }

    public static int[] squareStringToCoords(String square) {
        if (square.equals("-")) return null;
        char fileChar = square.charAt(0);
        char rankChar = square.charAt(1);
        int file = fileChar - 'a';
        int rank = 8 - Character.getNumericValue(rankChar);
        return new int[]{rank, file};
    }

    public static String coordsToSquareString(int[] square) {
        if (square == null || square.length != 2) return "-";
        char file = (char) ('a' + square[1]);
        int rank = 8 - square[0];
        return "" + file + rank;
    }

    public static String coordsToSquareString(int row, int col) {
        return "" + (char) ('a' + col) + (8 - row);
    }

    public static boolean isSquareAttacked(int[] square, Piece[][] position, Color attackerColor) {
        return isSquareAttacked(square[0], square[1], position, attackerColor);
    }

    public static boolean isSquareAttacked(int row, int col, Piece[][] position, Color attackerColor) {
        // Pawn
        int pawnRow = row + (attackerColor == Color.WHITE ? 1 : -1);
        int[] pawnCols = {col - 1, col + 1};

        for (int pawnCol : pawnCols) {
            if (inBounds(pawnRow, pawnCol)) {
                Piece p = position[pawnRow][pawnCol];
                if (p != null && p.getColor() == attackerColor && p.getType() == PieceType.PAWN) {
                    return true;
                }
            }
        }

        // Knight
        for (int[] jump : KNIGHT_JUMPS) {
            int r = row + jump[0];
            int c = col + jump[1];
            if (inBounds(r, c)) {
                Piece p = position[r][c];
                if (p != null && p.getColor() == attackerColor && p.getType() == PieceType.KNIGHT) {
                    return true;
                }
            }
        }

        // King
        for (int[] move : KING_JUMPS) {
            int r = row + move[0];
            int c = col + move[1];
            if (inBounds(r, c)) {
                Piece p = position[r][c];
                if (p != null && p.getColor() == attackerColor && p.getType() == PieceType.KING) {
                    return true;
                }
            }
        }

        // Rook and queen
        for (int[] dir : ROOK_DIRECTIONS) {
            int r = row + dir[0];
            int c = col + dir[1];
            while (inBounds(r, c)) {
                Piece p = position[r][c];
                if (p != null) {
                    if (p.getColor() == attackerColor &&
                            (p.getType() == PieceType.ROOK || p.getType() == PieceType.QUEEN)) {
                        return true;
                    }
                    break;
                }
                r += dir[0];
                c += dir[1];
            }
        }

        // Bishop and queen
        for (int[] dir : BISHOP_DIRECTIONS) {
            int r = row + dir[0];
            int c = col + dir[1];
            while (inBounds(r, c)) {
                Piece p = position[r][c];
                if (p != null) {
                    if (p.getColor() == attackerColor &&
                            (p.getType() == PieceType.BISHOP || p.getType() == PieceType.QUEEN)) {
                        return true;
                    }
                    break;
                }
                r += dir[0];
                c += dir[1];
            }
        }

        return false;
    }

    public static boolean inBounds(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    public static String createUciMove(int fromRow, int fromCol, int toRow, int toCol, Piece movedPiece) {
        String fromSquare = Utils.coordsToSquareString(fromRow, fromCol);
        String toSquare = Utils.coordsToSquareString(toRow, toCol);

        StringBuilder uci = new StringBuilder();
        uci.append(fromSquare).append(toSquare);

        // Promotion
        int lastRow = movedPiece.getColor() == Color.WHITE ? 0 : 7;
        if (movedPiece.getType() == PieceType.PAWN && toRow == lastRow) {

            // Default to queen promotion
            uci.append('q');
        }

        return uci.toString();
    }

    public static String moveToUci(Move move) {
        if (move == null) return "0000";

        String fromSquare = Utils.coordsToSquareString(move.getFromRow(), move.getFromCol());
        String toSquare = Utils.coordsToSquareString(move.getToRow(), move.getToCol());

        StringBuilder uci = new StringBuilder();
        uci.append(fromSquare).append(toSquare);

        if (move.isPromotion()) {
            PieceType pieceType = move.getPromotionPiece().getType();
            Piece piece = new Piece(pieceType, Color.BLACK);
            String pieceString = piece.toString();
            uci.append(pieceString);
        }

        return uci.toString();
    }

    public static Move uciToMove(String uciMove, BoardState boardState) {
        if (uciMove.length() < 4) {
            throw new IllegalArgumentException("Invalid UCI move: " + uciMove);
        }

        String fromSquare = uciMove.substring(0, 2);
        String toSquare = uciMove.substring(2, 4);

        int[] fromCoords = squareStringToCoords(fromSquare);
        int[] toCoords = squareStringToCoords(toSquare);

        int fromRow = fromCoords[0];
        int fromCol = fromCoords[1];
        int toRow = toCoords[0];
        int toCol = toCoords[1];
        Piece[][] position = boardState.getPosition();

        Piece movedPiece = position[fromRow][fromCol];
        Piece capturedPiece = position[toRow][toCol];

        // Promotion
        if (uciMove.length() == 5) {
            char promoChar = uciMove.charAt(4);
            PieceType promoType = stringToPiece("" + promoChar).getType();

            if (capturedPiece != null) {
                return Move.createPromotionCapture(fromRow, fromCol, toRow, toCol, movedPiece, capturedPiece, promoType);
            }

            return Move.createPromotionMove(fromRow, fromCol, toRow, toCol, movedPiece, promoType);
        }

        // Capture
        if (capturedPiece != null) {
            return Move.createCapture(fromRow, fromCol, toRow, toCol, movedPiece, capturedPiece);
        }

        // En passant
        if (movedPiece.getType() == PieceType.PAWN && fromCol != toCol) {
            int[] enPassantSquare = boardState.getEnPassantSquare();
            if (enPassantSquare != null && enPassantSquare[0] == toRow && enPassantSquare[1] == toCol) {
                return Move.createEnPassantCapture(fromRow, fromCol, toRow, toCol, movedPiece);
            }
        }

        // Castling
        if (movedPiece.getType() == PieceType.KING && Math.abs(toCol - fromCol) == 2) {
            if (toCol > fromCol) {
                return Move.createCastleKingSide(fromRow, fromCol, toRow, toCol, movedPiece);
            } else {
                return Move.createCastleQueenSide(fromRow, fromCol, toRow, toCol, movedPiece);
            }
        }

        return Move.createQuietMove(fromRow, fromCol, toRow, toCol, movedPiece);
    }
}
