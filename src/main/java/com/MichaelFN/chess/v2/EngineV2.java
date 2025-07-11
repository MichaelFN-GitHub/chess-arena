package com.MichaelFN.chess.v2;

import com.MichaelFN.chess.common.UciConnector;
import com.MichaelFN.chess.interfaces.NormalEvaluator;
import com.MichaelFN.chess.v1.EngineV1;
import com.MichaelFN.chess.v6.EngineV6;

import java.io.IOException;

import static com.MichaelFN.chess.common.Constants.DEBUG_ENGINES;

public class EngineV2 extends EngineV1 {
    private final NormalEvaluator evaluator = new Evaluator();
    private final Negamax searcher = new Negamax(evaluator);

    @Override
    public void startSearch(int depth, long timeLimitMillis) {
        if (DEBUG_ENGINES) System.out.println(getEngineName() + ": Search started...");
        nextMove = searcher.findBestMove(boardState, depth, timeLimitMillis);
        if (DEBUG_ENGINES) System.out.println(getEngineName() + ": Done searching.");
    }

    @Override
    public void clear() {
        searcher.clearTranspositionTable();
    }

    @Override
    public String getEngineName() {
        return "Material Eval Engine (V2)";
    }

    public static void main(String[] args) throws IOException {
        new UciConnector(new EngineV2()).loop();
    }
}
