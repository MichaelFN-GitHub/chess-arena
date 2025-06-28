package com.MichaelFN.chess.V1;

import java.util.List;

public class GameStatus {
    private boolean isCheckmate;
    private boolean isStalemate;
    private boolean isFiftyMoveRule;
    private boolean isInsufficientMaterial;

    private Color winner;

    public static GameStatus evaluateGameStatus(BoardState boardState) {
        GameStatus gameStatus = new GameStatus();

        Color playerToMove = boardState.getPlayerToMove();
        Piece[][] position = boardState.getPosition();

        // No legal moves
        List<Move> legalMoves = MoveGenerator.generateLegalMoves(boardState);
        if (legalMoves.isEmpty()) {
            int[] kingPosition = boardState.getKingPosition(playerToMove);
            Color opponentColor = playerToMove == Color.WHITE ? Color.BLACK : Color.WHITE;
            if (Utils.isSquareAttacked(kingPosition, position, opponentColor)) {
                // Checkmate
                gameStatus.setCheckmate(true);
                gameStatus.setWinner(opponentColor);
            } else {
                // Stalemate
                gameStatus.setStalemate(true);
            }
        }

        // Halfmove rule
        if (boardState.getHalfmoveClock() >= 50) gameStatus.setFiftyMoveRule(true);

        // Insufficient material (should take more scenarios into account than only kings)
        boolean onlyKings = true;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = position[i][j];
                if (piece != null && piece.type() != PieceType.KING) {
                    onlyKings = false;
                    break;
                }
            }
        }
        gameStatus.setInsufficientMaterial(onlyKings);

        return gameStatus;
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

    public void setCheckmate(boolean checkmate) {
        isCheckmate = checkmate;
    }

    public boolean isStalemate() {
        return isStalemate;
    }

    public void setStalemate(boolean stalemate) {
        isStalemate = stalemate;
    }

    public boolean isFiftyMoveRule() {
        return isFiftyMoveRule;
    }

    public void setFiftyMoveRule(boolean fiftyMoveRule) {
        isFiftyMoveRule = fiftyMoveRule;
    }

    public boolean isInsufficientMaterial() {
        return isInsufficientMaterial;
    }

    public void setInsufficientMaterial(boolean insufficientMaterial) {
        isInsufficientMaterial = insufficientMaterial;
    }

    public boolean isGameOver() {
        return isCheckmate || isStalemate || isFiftyMoveRule || isInsufficientMaterial;
    }

    public void setWinner(Color player) {
        winner = player;
    }

    public Color getWinner() {
        return winner;
    }
}
