package com.MichaelFN.chess.V1;

public record Piece(PieceType type, Color color) {

    @Override
    public String toString() {
        return Utils.pieceToString(this);
    }
}
