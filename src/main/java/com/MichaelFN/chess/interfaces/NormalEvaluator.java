package com.MichaelFN.chess.interfaces;

import com.MichaelFN.chess.v1.BoardState;

public interface NormalEvaluator {
    /**
     * Evaluates the position
     */
    int evaluate(BoardState boardState);
}
