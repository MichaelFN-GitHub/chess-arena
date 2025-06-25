package com.MichaelFN.chess.V1;

import java.util.HashMap;
import java.util.Map;

public class Utils {
    public static final int[][] KNIGHT_JUMPS = {{-2,-1}, {-2,1}, {-1,-2}, {-1,2}, {1,-2}, {1,2}, {2,-1}, {2,1}};
    public static final int[][] KING_JUMPS = {{1,0}, {-1,0}, {0,1}, {0,-1}, {1,1}, {1,-1}, {-1,1}, {-1,-1}};
    public static final int[][] BISHOP_DIRECTIONS = {{1,1}, {1,-1}, {-1,1}, {-1,-1}};
    public static final int[][] ROOK_DIRECTIONS = {{1,0}, {-1,0}, {0,1}, {0,-1}};

    public static final HashMap<String, Piece> stringToPieceMap = new HashMap<>() {{
        put("P", new Piece(PieceType.PAWN, Color.WHITE));
        put("p", new Piece(PieceType.PAWN, Color.BLACK));
        put("N", new Piece(PieceType.KNIGHT, Color.WHITE));
        put("n", new Piece(PieceType.KNIGHT, Color.BLACK));
        put("B", new Piece(PieceType.BISHOP, Color.WHITE));
        put("b", new Piece(PieceType.BISHOP, Color.BLACK));
        put("R", new Piece(PieceType.ROOK, Color.WHITE));
        put("r", new Piece(PieceType.ROOK, Color.BLACK));
        put("Q", new Piece(PieceType.QUEEN, Color.WHITE));
        put("q", new Piece(PieceType.QUEEN, Color.BLACK));
        put("K", new Piece(PieceType.KING, Color.WHITE));
        put("k", new Piece(PieceType.KING, Color.BLACK));
    }};

    public static final HashMap<Piece, String> pieceToStringMap = new HashMap<>();
    static {
        for (Map.Entry<String,Piece> entry : stringToPieceMap.entrySet()) {
            pieceToStringMap.put(entry.getValue(), entry.getKey());
        }
        pieceToStringMap.put(null, " ");
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

    public static boolean isSquareAttacked(int row, int col, Piece[][] position, Color attackerColor) {
        // Pawn
        int pawnDir = attackerColor == Color.WHITE ? -1 : 1;
        int[] pawnCols = {col - 1, col + 1};
        int pawnRow = row + pawnDir;

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
}
