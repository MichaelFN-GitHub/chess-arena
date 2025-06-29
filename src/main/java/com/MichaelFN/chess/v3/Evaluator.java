package com.MichaelFN.chess.v3;

import com.MichaelFN.chess.interfaces.NormalEvaluator;
import com.MichaelFN.chess.v1.BoardState;
import com.MichaelFN.chess.v1.Color;
import com.MichaelFN.chess.v1.Piece;

import static com.MichaelFN.chess.v3.EvaluationConstants.*;

public class Evaluator implements NormalEvaluator {

    @Override
    public int evaluate(BoardState boardState) {
        int score = 0;
        Color playerToMove = boardState.getPlayerToMove();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = boardState.getPiece(row, col);
                if (piece == null) continue;

                int pieceTypeIdx = piece.type().ordinal();
                Color pieceColor = piece.color();

                // Material
                int materialValue = PIECE_MATERIAL_VALUE[pieceTypeIdx];

                // Position
                int positionalTableRow = pieceColor == Color.WHITE ? row : 7 - row;
                int positionalValue = PIECE_POSITIONAL_VALUE[pieceTypeIdx][positionalTableRow][col];

                int value = materialValue + positionalValue;
                score += pieceColor == playerToMove ? value : -value;
            }
        }
        return score;
    }
}
