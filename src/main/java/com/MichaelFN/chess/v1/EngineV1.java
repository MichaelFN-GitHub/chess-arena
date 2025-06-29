package com.MichaelFN.chess.v1;

import com.MichaelFN.chess.interfaces.Engine;

import java.util.Collections;
import java.util.List;

public class EngineV1 implements Engine {
    protected BoardState boardState;
    protected Move nextMove;

    public EngineV1() {
        initialize();
    }

    @Override
    public void initialize() {
        boardState = new BoardState();
        System.out.println(getEngineName() + ": Initialized.");
    }

    @Override
    public void setPosition(String FEN) {
        boardState.parseFEN(FEN);
        System.out.println(getEngineName() + ": Position has been set.");
    }

    @Override
    public void startSearch(long timeLimitMillis) {
        // Chooses move at random
        System.out.println(getEngineName() + ": Search started...");
        List<Move> moves = MoveGenerator.generateLegalMoves(boardState);
        Collections.shuffle(moves);
        nextMove = moves.getFirst();
        System.out.println(getEngineName() + ": Done searching.");
    }

    @Override
    public String getMove() {
        return Utils.moveToUci(nextMove);
    }

    @Override
    public String getEngineName() {
        return "Random Move Engine";
    }

    @Override
    public String toString() {
        return getEngineName();
    }
}
