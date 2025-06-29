package com.MichaelFN.chess.v1;

import com.MichaelFN.chess.common.Color;

public class GameStatus {
    private final BoardState boardState;

    private boolean isCheckmate;
    private boolean isStalemate;
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

        // Halfmove rule
        if (boardState.getHalfmoveClock() >= 50) {
            isFiftyMoveRule = true;
            return;
        }

        // Insufficient material (should take more scenarios into account than only kings)
        isInsufficientMaterial = boardState.getRemainingPieces() == 2;
    }

    public void reset() {
        isCheckmate = false;
        isStalemate = false;
        isFiftyMoveRule = false;
        isInsufficientMaterial = false;
        winner = null;
    }

    public String getGameStatusMessage() {
        if (isCheckmate) return "Checkmate";
        if (isStalemate) return "Stalemate";
        if (isFiftyMoveRule || isInsufficientMaterial) return "Draw";
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
        return isCheckmate || isStalemate || isFiftyMoveRule || isInsufficientMaterial;
    }
}
