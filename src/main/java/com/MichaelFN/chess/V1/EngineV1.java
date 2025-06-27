package com.MichaelFN.chess.V1;

import com.MichaelFN.chess.EngineInterface;

import java.util.Collections;
import java.util.List;

public class EngineV1 implements EngineInterface {
    private BoardState boardState;
    private Move moveToPlay;

    @Override
    public void initialize() {
        System.out.println("Initializing...");
        boardState = new BoardState();
        System.out.println("Initialized.");
    }

    @Override
    public void setPosition(String FEN) {
        System.out.println("Setting position...");
        boardState.parseFEN(FEN);
        System.out.println("Position has been set.");
    }

    @Override
    public void startSearch(long timeLimitMillis) {
        // Chooses move at random
        System.out.println("Search started...");
        List<Move> moves = MoveGenerator.generateLegalMoves(boardState);
        Collections.shuffle(moves);
        moveToPlay = moves.getFirst();
        System.out.println("Search stopped.");
    }

    @Override
    public String getBestMove() {
        return Utils.moveToUci(moveToPlay);
    }
}
