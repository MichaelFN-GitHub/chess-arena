package com.MichaelFN.chess.v1;

public record Piece(PieceType type, Color color) {

    @Override
    public String toString() {
        return Utils.pieceToString(this);
    }
}
