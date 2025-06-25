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
                if (piece == null || piece.getColor() != playerToMove) {
                    continue;
                }

                switch (piece.getType()) {
                    case PAWN:
                        generatePawnMoves(piece, i, j, position);
                        break;
                    case KNIGHT:
                        generateKnightMoves(piece, i, j, position);
                        break;
                    case BISHOP:
                        generateBishopMoves(piece, i, j, position);
                        break;
                    case ROOK:
                        generateRookMoves(piece, i, j, position);
                        break;
                    case QUEEN:
                        generateQueenMoves(piece, i, j, position);
                        break;
                    case KING:
                        generateKingMoves(piece, i, j, position);
                        break;
                }
            }
        }

        return moveList;
    }

    private static void generateKingMoves(Piece piece, int row, int col, Piece[][] position) {
    }

    private static void generateQueenMoves(Piece piece, int row, int col, Piece[][] position) {
    }

    private static void generateRookMoves(Piece piece, int row, int col, Piece[][] position) {
    }

    private static void generateBishopMoves(Piece piece, int row, int col, Piece[][] position) {
    }

    private static void generateKnightMoves(Piece piece, int row, int col, Piece[][] position) {
    }

    public static void generatePawnMoves(Piece pawn, int row, int col, Piece[][] position) {
        int forward = pawn.getColor() == Color.WHITE ? -1 : 1;
        boolean hasNotMoved = pawn.getColor() == Color.WHITE ? row == 6 : row == 1;
        int toRow;
        int toCol;

        // Push one square
        toRow = row + forward;
        moveList.add(Move.createQuietMove(row, col, toRow, col, pawn));

        // Push two squares
        if (hasNotMoved && position[row + forward][col] == null) {
            toRow = row + 2*forward;
            moveList.add(Move.createDoublePawnPush(row, col, toRow, col, pawn));
        }

        // Capture to the right
        toRow = row + forward;
        toCol = col + 1;
        if (toCol < 8) {
            Piece capturedPiece = position[toRow][toCol];
            if (capturedPiece != null && capturedPiece.getColor() != pawn.getColor()) {
                moveList.add(Move.createCapture(row, col, toRow, toCol, pawn, capturedPiece));
            }
        }

        // Capture to the left
        toRow = row + forward;
        toCol = col - 1;
        if (toCol >= 0) {
            Piece capturedPiece = position[toRow][toCol];
            if (capturedPiece != null && capturedPiece.getColor() != pawn.getColor()) {
                moveList.add(Move.createCapture(row, col, toRow, toCol, pawn, capturedPiece));
            }
        }

        // En passant (later)
    }
}
