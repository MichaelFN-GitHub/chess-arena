package com.MichaelFN.chess.V1;

import java.util.ArrayList;
import java.util.List;

public class MoveGenerator {

    public static List<Move> generateMoves(BoardState boardState) {
        List<Move> moveList = new ArrayList<>();

        Color playerToMove = boardState.getPlayerToMove();
        Piece[][] position = boardState.getPosition();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = position[i][j];
                if (piece == null || piece.getColor() != playerToMove) continue;

                switch (piece.getType()) {
                    case PAWN:
                        generatePawnMoves(moveList, piece, i, j, position);
                        // TODO: En passant
                        break;
                    case KNIGHT:
                        generateKnightMoves(moveList, piece, i, j, position);
                        break;
                    case BISHOP:
                        generateBishopMoves(moveList, piece, i, j, position);
                        break;
                    case ROOK:
                        generateRookMoves(moveList, piece, i, j, position);
                        break;
                    case QUEEN:
                        generateQueenMoves(moveList, piece, i, j, position);
                        break;
                    case KING:
                        generateKingMoves(moveList, piece, i, j, position);
                        // TODO: Castling
                        break;
                }
            }
        }

        return moveList;
    }

    private static void generateKingMoves(List<Move> moveList, Piece king, int row, int col, Piece[][] position) {
        int[][] jumps = {{1,0}, {-1,0}, {0,1}, {0,-1}, {1,1}, {1,-1}, {-1,1}, {-1,-1}};
        generateJumpMoves(moveList, king, row, col, position, jumps);
    }

    private static void generateKnightMoves(List<Move> moveList, Piece knight, int row, int col, Piece[][] position) {
        int[][] jumps = {{-2,-1}, {-2,1}, {-1,-2}, {-1,2}, {1,-2}, {1,2}, {2,-1}, {2,1}};
        generateJumpMoves(moveList, knight, row, col, position, jumps);
    }

    private static void generateJumpMoves(List<Move> moveList, Piece piece, int row, int col, Piece[][] position, int[][] jumps) {
        for (int[] jump : jumps) {
            int toRow = row + jump[0];
            int toCol = col + jump[1];
            if (!Utils.inBounds(toRow, toCol)) continue;

            Piece target = position[toRow][toCol];
            if (target == null) {
                moveList.add(Move.createQuietMove(row, col, toRow, toCol, piece));
            } else if (target.getColor() != piece.getColor()) {
                moveList.add(Move.createCapture(row, col, toRow, toCol, piece, target));
            }
        }
    }

    private static void generateQueenMoves(List<Move> moveList, Piece queen, int row, int col, Piece[][] position) {
        generateRookMoves(moveList, queen, row, col, position);
        generateBishopMoves(moveList, queen, row, col, position);
    }

    private static void generateRookMoves(List<Move> moveList, Piece rook, int row, int col, Piece[][] position) {
        int[][] directions = {{1,0}, {-1,0}, {0,1}, {0,-1}};
        generateSlidingMoves(moveList, rook, row, col, position, directions);
    }

    private static void generateBishopMoves(List<Move> moveList, Piece bishop, int row, int col, Piece[][] position) {
        int[][] directions = {{1,1}, {1,-1}, {-1,1}, {-1,-1}};
        generateSlidingMoves(moveList, bishop, row, col, position, directions);
    }

    private static void generateSlidingMoves(List<Move> moveList, Piece piece, int row, int col, Piece[][] position, int[][] directions) {
        for (int[] dir : directions) {
            int toRow = row + dir[0];
            int toCol = col + dir[1];
            while (Utils.inBounds(toRow, toCol)) {
                Piece capturedPiece = position[toRow][toCol];
                if (capturedPiece == null) {
                    moveList.add(Move.createQuietMove(row, col, toRow, toCol, piece));
                } else {
                    if (capturedPiece.getColor() != piece.getColor()) {
                        moveList.add(Move.createCapture(row, col, toRow, toCol, piece, capturedPiece));
                    }
                    break;
                }
                toRow += dir[0];
                toCol += dir[1];
            }
        }
    }

    public static void generatePawnMoves(List<Move> moveList, Piece pawn, int row, int col, Piece[][] position) {
        int forward = pawn.getColor() == Color.WHITE ? -1 : 1;
        boolean hasNotMoved = pawn.getColor() == Color.WHITE ? row == 6 : row == 1;
        int promotionRow = pawn.getColor() == Color.WHITE ? 0 : 7;

        // Push one square
        int toRow = row + forward;
        if (position[toRow][col] == null) {
            if (toRow == promotionRow) {
                moveList.add(Move.createPromotionMove(row, col, toRow, col, pawn, PieceType.QUEEN));
                moveList.add(Move.createPromotionMove(row, col, toRow, col, pawn, PieceType.ROOK));
                moveList.add(Move.createPromotionMove(row, col, toRow, col, pawn, PieceType.BISHOP));
                moveList.add(Move.createPromotionMove(row, col, toRow, col, pawn, PieceType.KNIGHT));

            } else {
                moveList.add(Move.createQuietMove(row, col, toRow, col, pawn));
            }

            // Push two squares
            toRow = row + 2*forward;
            if (hasNotMoved && position[toRow][col] == null) {
                moveList.add(Move.createDoublePawnPush(row, col, toRow, col, pawn));
            }
        }

        // Capture to the right
        toRow = row + forward;
        int toCol = col + 1;
        if (toCol < 8) {
            Piece capturedPiece = position[toRow][toCol];
            if (capturedPiece != null && capturedPiece.getColor() != pawn.getColor()) {
                if (toRow == promotionRow) {
                    moveList.add(Move.createPromotionCapture(row, col, toRow, toCol, pawn, capturedPiece, PieceType.QUEEN));
                    moveList.add(Move.createPromotionCapture(row, col, toRow, toCol, pawn, capturedPiece, PieceType.ROOK));
                    moveList.add(Move.createPromotionCapture(row, col, toRow, toCol, pawn, capturedPiece, PieceType.BISHOP));
                    moveList.add(Move.createPromotionCapture(row, col, toRow, toCol, pawn, capturedPiece, PieceType.KNIGHT));
                } else {
                    moveList.add(Move.createCapture(row, col, toRow, toCol, pawn, capturedPiece));
                }
            }
        }

        // Capture to the left
        toRow = row + forward;
        toCol = col - 1;
        if (toCol >= 0) {
            Piece capturedPiece = position[toRow][toCol];
            if (capturedPiece != null && capturedPiece.getColor() != pawn.getColor()) {
                if (toRow == promotionRow) {
                    moveList.add(Move.createPromotionCapture(row, col, toRow, toCol, pawn, capturedPiece, PieceType.QUEEN));
                    moveList.add(Move.createPromotionCapture(row, col, toRow, toCol, pawn, capturedPiece, PieceType.ROOK));
                    moveList.add(Move.createPromotionCapture(row, col, toRow, toCol, pawn, capturedPiece, PieceType.BISHOP));
                    moveList.add(Move.createPromotionCapture(row, col, toRow, toCol, pawn, capturedPiece, PieceType.KNIGHT));
                } else {
                    moveList.add(Move.createCapture(row, col, toRow, toCol, pawn, capturedPiece));
                }
            }
        }
    }
}
