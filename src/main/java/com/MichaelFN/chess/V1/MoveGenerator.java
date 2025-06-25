package com.MichaelFN.chess.V1;

import java.util.List;

public class MoveGenerator {
    public static List<Move> moveList;

    public static void generatePawnMoves(Piece pawn, int rank, int file, Piece[][] position) {
        int forward = pawn.getColor() == Color.WHITE ? 1 : -1;
        boolean isFirstRank = pawn.getColor() == Color.WHITE ? rank == 1 : rank == 6;
        int toRank;
        int toFile;

        // Push one square
        toRank = rank + forward;
        moveList.add(Move.createQuietMove(rank, file, toRank, file, pawn));

        // Push two squares
        if (isFirstRank) {
            toRank = rank + 2*forward;
            moveList.add(Move.createDoublePawnPush(rank, file, toRank, file, pawn));
        }

        // Capture to the right
        toRank = rank + forward;
        toFile = file + 1;
        if (toFile < 8) {
            Piece capturedPiece = position[toRank][toFile];
            if (capturedPiece != null && capturedPiece.getColor() != pawn.getColor()) {
                moveList.add(Move.createCapture(rank, file, toRank, toFile, pawn, capturedPiece));
            }
        }

        // Capture to the left
        toRank = rank + forward;
        toFile = file - 1;
        if (toFile >= 0) {
            Piece capturedPiece = position[toRank][toFile];
            if (capturedPiece != null && capturedPiece.getColor() != pawn.getColor()) {
                moveList.add(Move.createCapture(rank, file, toRank, toFile, pawn, capturedPiece));
            }
        }

        // En passant (later)
    }
}
