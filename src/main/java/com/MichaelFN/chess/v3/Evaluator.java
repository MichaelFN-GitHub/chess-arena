package com.MichaelFN.chess.v3;

import com.MichaelFN.chess.interfaces.NormalEvaluator;
import com.MichaelFN.chess.v1.*;

import static com.MichaelFN.chess.v3.EvaluationConstants.*;

public class Evaluator implements NormalEvaluator {

    @Override
    public int evaluate(BoardState boardState) {
        int score = 0;
        Color playerToMove = boardState.getPlayerToMove();
        int playerColorIdx = playerToMove.ordinal();
        int[][] pawnsOnRow = new int[2][8];
        int[] bishops = new int[2];

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = boardState.getPiece(row, col);
                if (piece == null) continue;

                PieceType pieceType = piece.type();
                Color pieceColor = piece.color();
                int pieceTypeIdx = pieceType.ordinal();
                int pieceColorIdx = pieceColor.ordinal();
                boolean isPlayerPiece = pieceColor == playerToMove;

                // Material
                int materialValue = PIECE_MATERIAL_VALUE[pieceTypeIdx];

                // Position
                int positionalTableRow = pieceColor == Color.WHITE ? row : 7 - row;
                int positionalValue = PIECE_POSITIONAL_VALUE[pieceTypeIdx][positionalTableRow][col];

                // Pawn structure
                if (pieceType == PieceType.PAWN) {
                    pawnsOnRow[playerColorIdx][col]++;
                }

                // Bishop pair
                if (pieceType == PieceType.BISHOP) {
                    bishops[isPlayerPiece ? 0 : 1]++;
                }

                int value = materialValue + positionalValue;
                score += isPlayerPiece ? value : -value;
            }
        }

        // Pawn structure
        for (int i = 0; i < 8; i++) {

            // Doubled pawns
            score -= (Math.max(1, pawnsOnRow[0][i]) - 1) * 15;
            score += (Math.max(1, pawnsOnRow[1][i]) - 1) * 15;

            // Isolated pawns
            if ((i == 0 || pawnsOnRow[0][i-1] == 0) && (i == 7 || pawnsOnRow[0][i+1] == 0)) score -= 10;
            if ((i == 0 || pawnsOnRow[1][i-1] == 0) && (i == 7 || pawnsOnRow[1][i+1] == 0)) score += 10;
        }

        // Bishop pair
        if (bishops[0] >= 2) score += 50;
        if (bishops[1] >= 2) score -= 50;

        // Tempo
        score += 5;

        return score;
    }
}
