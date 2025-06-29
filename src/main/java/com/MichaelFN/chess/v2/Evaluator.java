package com.MichaelFN.chess.v2;

import com.MichaelFN.chess.v1.*;

public class Evaluator implements com.MichaelFN.chess.interfaces.NormalEvaluator {
    public static final int[] PIECE_VALUES = {10, 30, 35, 50, 90, 0};

    @Override
    public int evaluate(BoardState boardState) {
        return getMaterialCount(boardState);
    }

    private int getMaterialCount(BoardState boardState) {
        int materialCount = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = boardState.getPiece(i,j);
                if (piece == null) continue;
                int pieceValue = PIECE_VALUES[piece.type().ordinal()];
                materialCount += piece.color() == boardState.getPlayerToMove() ? pieceValue : -pieceValue;
            }
        }
        return materialCount;
    }
}
