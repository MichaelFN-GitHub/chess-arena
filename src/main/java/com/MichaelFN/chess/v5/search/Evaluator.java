package com.MichaelFN.chess.v5.search;

import com.MichaelFN.chess.v5.board.Bitboard;
import com.MichaelFN.chess.v5.board.Board;

import static com.MichaelFN.chess.v5.Constants.ALL_PIECES;
import static com.MichaelFN.chess.v5.Constants.WHITE;

public class Evaluator {
    private int[] pieceValue = {0, 100, 320, 330, 500, 900, 0};

    public int evaluate(Board board) {
        return 0;
    }
}
