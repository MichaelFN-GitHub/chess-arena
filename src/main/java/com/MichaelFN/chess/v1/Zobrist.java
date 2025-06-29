package com.MichaelFN.chess.v1;

import java.util.Random;

public class Zobrist {
    private static final int BOARD_SQUARES = 64;
    private static final int PIECE_TYPES = 12;

    private static final long[][] pieceSquareRandoms;
    private static final long[] castlingRightsRandoms;
    private static final long[] enPassantFileRandoms;
    private static final long sideToMoveRandom;

    static {
        Random random = new Random(0);

        pieceSquareRandoms = new long[PIECE_TYPES][BOARD_SQUARES];
        for (int piece = 0; piece < PIECE_TYPES; piece++) {
            for (int sq = 0; sq < BOARD_SQUARES; sq++) {
                pieceSquareRandoms[piece][sq] = random.nextLong();
            }
        }

        castlingRightsRandoms = new long[4];
        for (int i = 0; i < 4; i++) {
            castlingRightsRandoms[i] = random.nextLong();
        }

        enPassantFileRandoms = new long[8];
        for (int i = 0; i < 8; i++) {
            enPassantFileRandoms[i] = random.nextLong();
        }

        sideToMoveRandom = random.nextLong();
    }

    public static int pieceToIndex(Piece piece) {
        int base = piece.color() == Color.WHITE ? 0 : 6;
        return base + piece.type().ordinal();
    }

    public static long computeHash(BoardState boardState) {
        long h = 0L;

        Piece[][] position = boardState.getPosition();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = position[row][col];
                if (piece != null) {
                    int pieceIndex = pieceToIndex(piece);
                    int sq = row * 8 + col;
                    h ^= pieceSquareRandoms[pieceIndex][sq];
                }
            }
        }

        // Side to move
        if (boardState.getPlayerToMove() == Color.BLACK) {
            h ^= sideToMoveRandom;
        }

        // Castling rights
        boolean[][] cr = boardState.getCastlingRights();
        if (cr[0][0]) h ^= castlingRightsRandoms[0];
        if (cr[0][1]) h ^= castlingRightsRandoms[1];
        if (cr[1][0]) h ^= castlingRightsRandoms[2];
        if (cr[1][1]) h ^= castlingRightsRandoms[3];

        // En passant (only file matters)
        int[] ep = boardState.getEnPassantSquare();
        if (ep != null) {
            int epFile = ep[1];
            if (epFile >= 0 && epFile < 8) {
                h ^= enPassantFileRandoms[epFile];
            }
        }

        return h;
    }
}
