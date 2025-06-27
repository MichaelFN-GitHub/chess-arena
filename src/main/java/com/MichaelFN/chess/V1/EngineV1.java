package com.MichaelFN.chess.V1;

import com.MichaelFN.chess.Interfaces.EngineInterface;

import java.util.Collections;
import java.util.List;

public class EngineV1 implements EngineInterface {
    private BoardState boardState;
    private Move moveToPlay;

    @Override
    public void initialize() {
        boardState = new BoardState();
        System.out.println("V1: Initialized.");
    }

    @Override
    public void setPosition(String FEN) {
        boardState.parseFEN(FEN);
        System.out.println("V1: Position has been set.");
    }

    @Override
    public void startSearch(long timeLimitMillis) {
        // Chooses move at random
        System.out.println("V1: Search started...");
        List<Move> moves = MoveGenerator.generateLegalMoves(boardState);
        Collections.shuffle(moves);
        moveToPlay = moves.getFirst();
        System.out.println("V1: Search stopped.");
    }

    @Override
    public String getBestMove() {
        return Utils.moveToUci(moveToPlay);
    }
}
