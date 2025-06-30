package com.MichaelFN.chess.v1;

import java.util.HashMap;

public class GameStatus {
    private final BoardState boardState;

    private boolean isCheckmate;
    private boolean isStalemate;
    private boolean isRepetition;
    private boolean isFiftyMoveRule;
    private boolean isInsufficientMaterial;

    private Color winner;

    public GameStatus(BoardState boardState) {
        this.boardState = boardState;
    }

    public void evaluateGameStatus() {
        Color playerToMove = boardState.getPlayerToMove();
        Piece[][] position = boardState.getPosition();

        // No legal moves
        if (!boardState.hasLegalMove()) {
            int[] kingPosition = boardState.getKingPosition(playerToMove);
            Color opponentColor = playerToMove == Color.WHITE ? Color.BLACK : Color.WHITE;
            if (Utils.isSquareAttacked(kingPosition, position, opponentColor)) {
                // Checkmate
                isCheckmate = true;
                winner = opponentColor;
            } else {
                // Stalemate
                isStalemate = true;
            }
            return;
        }

        // Threefold repetition
        if (boardState.checkRepetition()) {
            isRepetition = true;
            return;
        }

        // Halfmove rule
        if (boardState.getHalfmoveClock() >= 50) {
            isFiftyMoveRule = true;
            return;
        }

        // Insufficient material (should take more scenarios into account than only kings)
        int pieceCount = 0;
        int[][] remainingPieces = boardState.getRemainingPieces();
        for (int i = 0; i < 6; i++) {
            pieceCount += remainingPieces[0][i] + remainingPieces[1][i];
        }
        isInsufficientMaterial = pieceCount == 2;
    }

    public void reset() {
        isCheckmate = false;
        isStalemate = false;
        isRepetition = false;
        isFiftyMoveRule = false;
        isInsufficientMaterial = false;
        winner = null;
    }

    public String getGameStatusMessage() {
        if (isCheckmate) return "Checkmate";
        if (isStalemate) return "Stalemate";
        if (isRepetition || isFiftyMoveRule || isInsufficientMaterial) return "Draw";
        return "Ongoing";
    }

    public String getWinnerString() {
        if (winner == null) return "Draw";
        return winner == Color.WHITE ? "White" : "Black";
    }

    public boolean isCheckmate() {
        return isCheckmate;
    }

    public boolean isGameOver() {
        return isCheckmate || isStalemate || isRepetition || isFiftyMoveRule || isInsufficientMaterial;
    }
}
