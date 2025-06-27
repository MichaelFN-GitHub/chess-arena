package com.MichaelFN.chess.V1;

public class GameStatus {
    private boolean isCheckmate;
    private boolean isStalemate;
    private boolean isFiftyMoveRule;
    private boolean isInsufficientMaterial;

    public String getGameStatus() {
        if (isCheckmate) return "Checkmate";
        if (isStalemate) return "Stalemate";
        if (isFiftyMoveRule || isInsufficientMaterial) return "Draw";
        return "Ongoing";
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
}
