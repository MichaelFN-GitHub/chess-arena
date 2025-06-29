package com.MichaelFN.chess.v1;

import com.MichaelFN.chess.common.Color;
import com.MichaelFN.chess.common.PieceType;

public record Piece(PieceType type, Color color) {

    @Override
    public String toString() {
        return Utils.pieceToString(this);
    }
}
