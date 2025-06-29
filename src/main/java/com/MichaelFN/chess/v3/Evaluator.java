package com.MichaelFN.chess.v3;

import com.MichaelFN.chess.interfaces.NormalEvaluator;
import com.MichaelFN.chess.v1.*;

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

                PieceType pieceType = piece.type();
                Color pieceColor = piece.color();
                int pieceTypeIdx = pieceType.ordinal();
                boolean isPlayerPiece = pieceColor == playerToMove;

                // Material
                int materialValue = PIECE_MATERIAL_VALUE[pieceTypeIdx];

                // Position
                int positionalTableRow = pieceColor == Color.WHITE ? row : 7 - row;
                int positionalValue = PIECE_POSITIONAL_VALUE[pieceTypeIdx][positionalTableRow][col];

                int value = materialValue + positionalValue;
                score += isPlayerPiece ? value : -value;
            }
        }

        return score;
    }
}
