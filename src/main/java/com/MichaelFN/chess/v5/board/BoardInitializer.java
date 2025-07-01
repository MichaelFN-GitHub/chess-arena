package com.MichaelFN.chess.v5.board;

import com.MichaelFN.chess.v5.Utils;

import static com.MichaelFN.chess.v5.Constants.*;

public class BoardInitializer {
    public static final String START_POS = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    public static void initializeBoard(Board board) {
        initializeBoard(board, START_POS);
    }

    public static void initializeBoard(Board board, String FEN) {
        String[] parts = FEN.split(" ");
        if (parts.length < 6) {
            throw new IllegalArgumentException("Invalid FEN");
        }

        board.clear();

        String[] fenRanks = parts[0].split("/");
        for (int row = 0; row < 8; row++) {
            int col = 0;
            for (int j = 0; j < fenRanks[row].length(); j++) {
                char c = fenRanks[row].charAt(j);
                if (Character.isDigit(c)) {
                    col += Character.getNumericValue(c);
                } else {
                    int pieceColor = Character.isUpperCase(c) ? WHITE : BLACK;
                    int pieceType = Utils.charToPieceType(c);
                    int rank = 7 - row;
                    int square = rank*8 + col;
                    long bb = Bitboard.squareToBitboard(square);
                    board.pieces[pieceColor][pieceType] |= bb;
                    board.pieces[pieceColor][ALL_PIECES] |= bb;
                    col++;
                }
            }
        }

        board.playerToMove = parts[1].equals("w") ? WHITE : BLACK;
        if (parts[2].contains("K")) board.castlingRights |= 0b1000;
        if (parts[2].contains("Q")) board.castlingRights |= 0b0100;
        if (parts[2].contains("k")) board.castlingRights |= 0b0010;
        if (parts[2].contains("q")) board.castlingRights |= 0b0001;
        board.enPassantSquare = parts[3].equals("-") ? -1 : Utils.algebraicToSquare(parts[3]);
        board.halfmoveClock = Integer.parseInt(parts[4]);
        board.fullmoveNumber = Integer.parseInt(parts[5]);
    }
}
