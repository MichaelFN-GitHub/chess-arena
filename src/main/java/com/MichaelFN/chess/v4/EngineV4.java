package com.MichaelFN.chess.v4;

import com.MichaelFN.chess.interfaces.NormalEvaluator;
import com.MichaelFN.chess.v2.Negamax;
import com.MichaelFN.chess.v3.EngineV3;

import static com.MichaelFN.chess.common.Constants.DEBUG_ENGINES;

public class EngineV4 extends EngineV3 {
    private final NormalEvaluator evaluator = new Evaluator();
    private final Negamax searcher = new Negamax(evaluator);

    @Override
    public void startSearch(int depth, long timeLimitMillis) {
        if (DEBUG_ENGINES) System.out.println(getEngineName() + ": Search started...");
        nextMove = searcher.findBestMove(boardState, depth, timeLimitMillis);
        if (DEBUG_ENGINES) System.out.println(getEngineName() + ": Done searching.");
    }

    @Override
    public String getEngineName() {
        return "Best Normal Engine (V4)";
    }
}
