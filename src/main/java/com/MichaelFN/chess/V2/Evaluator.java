package com.MichaelFN.chess.V2;

import com.MichaelFN.chess.V1.*;

import java.util.HashMap;

public class Evaluator {
    public static final int[] PIECE_VALUES = {10, 30, 35, 50, 90, 0};

    public int evaluate(BoardState boardState) {
        return getMaterialCount(boardState.getPosition(), boardState.getPlayerToMove());
    }

    private int getMaterialCount(Piece[][] position, Color playerColor) {
        int materialCount = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = position[i][j];
                if (piece == null) continue;
                int pieceValue = PIECE_VALUES[piece.type().ordinal()];
                materialCount += piece.color() == playerColor ? pieceValue : -pieceValue;
            }
        }
        return materialCount;
    }
}
