package com.MichaelFN.chess.V1;

import java.util.HashMap;
import java.util.Map;

public class Utils {
    public static final HashMap<String, Piece> stringToPieceMap = new HashMap<>() {{
        put("P", new Piece(PieceType.PAWN, Color.WHITE));
        put("p", new Piece(PieceType.PAWN, Color.BLACK));
        put("N", new Piece(PieceType.KNIGHT, Color.WHITE));
        put("n", new Piece(PieceType.KNIGHT, Color.BLACK));
        put("B", new Piece(PieceType.BISHOP, Color.WHITE));
        put("b", new Piece(PieceType.BISHOP, Color.BLACK));
        put("R", new Piece(PieceType.ROOK, Color.WHITE));
        put("r", new Piece(PieceType.ROOK, Color.BLACK));
        put("Q", new Piece(PieceType.QUEEN, Color.WHITE));
        put("q", new Piece(PieceType.QUEEN, Color.BLACK));
        put("K", new Piece(PieceType.KING, Color.WHITE));
        put("k", new Piece(PieceType.KING, Color.BLACK));
    }};

    public static final HashMap<Piece, String> pieceToStringMap = new HashMap<>();
    static {
        for (Map.Entry<String,Piece> entry : stringToPieceMap.entrySet()) {
            pieceToStringMap.put(entry.getValue(), entry.getKey());
        }
        pieceToStringMap.put(null, " ");
    }
}
