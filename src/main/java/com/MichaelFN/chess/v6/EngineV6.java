package com.MichaelFN.chess.v6;

import com.MichaelFN.chess.v5.Constants;
import com.MichaelFN.chess.v5.EngineV5;
import com.MichaelFN.chess.v5.search.Evaluator;

import static com.MichaelFN.chess.common.Constants.DEBUG_ENGINES;

public class EngineV6 extends EngineV5 {
    private final Evaluator evaluator = new Evaluator();
    private final Searcher searcher = new Searcher(evaluator);

    @Override
    public void startSearch(int depth, long timeLimitMillis) {
        if (DEBUG_ENGINES) System.out.println(getEngineName() + ": Search started...");
        bestMove = searcher.negamax(board, depth, timeLimitMillis);
        if (DEBUG_ENGINES) System.out.println(getEngineName() + ": Done searching.");
    }

    @Override
    public void clear() {
        searcher.clear();
    }

    @Override
    public String getEngineName() {
        return "Faster Bitboard Engine (V6)";
    }
}
