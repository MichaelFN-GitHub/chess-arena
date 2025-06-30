package com.MichaelFN.chess.v4;

import com.MichaelFN.chess.interfaces.NormalEvaluator;
import com.MichaelFN.chess.v1.BoardState;
import com.MichaelFN.chess.v1.Color;
import com.MichaelFN.chess.v1.Piece;
import com.MichaelFN.chess.v1.PieceType;

public class Evaluator implements NormalEvaluator {

    @Override
    public int evaluate(BoardState boardState) {
        int mgScore = 0;
        int egScore = 0;

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
                int mgMaterialValue = PestoConstants.MG_VALUE[pieceTypeIdx];
                int egMaterialValue = PestoConstants.EG_VALUE[pieceTypeIdx];

                // Position
                int positionalTableRow = pieceColor == Color.WHITE ? row : 7 - row;
                int mgPositionalValue = PestoConstants.MG_PIECE_TABLES[pieceTypeIdx][positionalTableRow][col];
                int egPositionalValue = PestoConstants.EG_PIECE_TABLES[pieceTypeIdx][positionalTableRow][col];

                // Pawn structure
                if (pieceType == PieceType.PAWN) {
                    pawnsOnRow[isPlayerPiece ? 0 : 1][col]++;
                }

                // Bishop pair
                if (pieceType == PieceType.BISHOP) {
                    bishops[isPlayerPiece ? 0 : 1]++;
                }

                int mgValue = mgMaterialValue + mgPositionalValue;
                int egValue = egMaterialValue + egPositionalValue;
                mgScore += isPlayerPiece ? mgValue : -mgValue;
                egScore += isPlayerPiece ? egValue : -egValue;
            }
        }

        // Taper evaluation based on game phase
        int gamePhase = 0;
        int[][] remainingPieces = boardState.getRemainingPieces();
        for (int type = 0; type < 6; type++) {
            int remainingPieceType = remainingPieces[0][type] + remainingPieces[1][type];
            gamePhase += PestoConstants.GAME_PHASE_VALUES[type] * remainingPieceType;
        }

        int mgPhase = Math.min(24, gamePhase);
        int egPhase = 24 - mgPhase;
        int score = (mgScore*mgPhase + egScore*egPhase) / 24;


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
        if (bishops[0] >= 2) score += 30;
        if (bishops[1] >= 2) score -= 30;

        return score;
    }
}
