package com.MichaelFN.chess.v2;

import com.MichaelFN.chess.interfaces.NormalEvaluator;
import com.MichaelFN.chess.v1.EngineV1;

import static com.MichaelFN.chess.common.Constants.DEBUG_ENGINES;

public class EngineV2 extends EngineV1 {
    private final NormalEvaluator evaluator = new Evaluator();
    private final Negamax searcher = new Negamax(evaluator);

    @Override
    public void startSearch(long timeLimitMillis) {
        if (DEBUG_ENGINES) System.out.println(getEngineName() + ": Search started...");
        nextMove = searcher.findBestMove(boardState, 100, timeLimitMillis);
        if (DEBUG_ENGINES) System.out.println(getEngineName() + ": Done searching.");
    }

    @Override
    public void clear() {
        searcher.clearTranspositionTable();
    }

    @Override
    public String getEngineName() {
        return "Material Eval Engine";
    }
}
