package com.MichaelFN.chess.v3;

import com.MichaelFN.chess.interfaces.NormalEvaluator;
import com.MichaelFN.chess.v2.EngineV2;
import com.MichaelFN.chess.v2.Negamax;

import static com.MichaelFN.chess.common.Constants.DEBUG_ENGINES;

public class EngineV3 extends EngineV2 {
    private final NormalEvaluator evaluator = new Evaluator();
    private final Negamax searcher = new Negamax(evaluator);

    @Override
    public void startSearch(long timeLimitMillis) {
        if (DEBUG_ENGINES) System.out.println(getEngineName() + ": Search started...");
        nextMove = searcher.findBestMove(boardState, 100, timeLimitMillis);
        if (DEBUG_ENGINES) System.out.println(getEngineName() + ": Done searching.");
    }

    @Override
    public String getEngineName() {
        return "Positional Eval Engine";
    }
}
