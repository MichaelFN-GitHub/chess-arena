package com.MichaelFN.chess.V2;

import com.MichaelFN.chess.V1.EngineV1;
import com.MichaelFN.chess.V1.MoveGenerator;

public class EngineV2 extends EngineV1 {

    @Override
    public void startSearch(long timeLimitMillis) {
        nextMove = MoveGenerator.generateLegalMoves(boardState).getFirst();

        // Implement minimax with simple evaluator
    }

    @Override
    public String getEngineName() {
        return "Material Eval Engine";
    }
}
