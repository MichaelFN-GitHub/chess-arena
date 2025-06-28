package com.MichaelFN.chess.V2;

import com.MichaelFN.chess.V1.EngineV1;

public class EngineV2 extends EngineV1 {
    private final Evaluator evaluator = new Evaluator();
    private final Negamax searcher = new Negamax(evaluator);

    @Override
    public void startSearch(long timeLimitMillis) {
        System.out.println(getEngineName() + ": Search started...");
        nextMove = searcher.findBestMove(boardState, 100, timeLimitMillis);
        System.out.println(getEngineName() + ": Done searching.");
    }

    @Override
    public String getEngineName() {
        return "Material Eval Engine";
    }
}
