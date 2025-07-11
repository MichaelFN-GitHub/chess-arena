package com.MichaelFN.chess.v1;

import com.MichaelFN.chess.common.UciConnector;
import com.MichaelFN.chess.interfaces.Engine;
import com.MichaelFN.chess.v6.EngineV6;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import static com.MichaelFN.chess.common.Constants.DEBUG_ENGINES;

public class EngineV1 implements Engine {
    protected BoardState boardState;
    protected Move nextMove;

    public EngineV1() {
        initialize();
    }

    @Override
    public void initialize() {
        boardState = new BoardState();
        if (DEBUG_ENGINES) System.out.println(getEngineName() + ": Initialized.");
    }

    @Override
    public void setPosition(String FEN, Stack<String> uciMoves) {
        boardState.parseFEN(FEN);
        for (String move : uciMoves) {
            boardState.makeMove(Utils.uciToMove(move, boardState));
        }
        if (DEBUG_ENGINES) System.out.println(getEngineName() + ": Position has been set.");
    }

    @Override
    public void startSearch(int depth, long timeLimitMillis) {
        // Chooses move at random
        if (DEBUG_ENGINES) System.out.println(getEngineName() + ": Search started...");
        List<Move> moves = MoveGenerator.generateLegalMoves(boardState);
        Collections.shuffle(moves);
        nextMove = moves.getFirst();
        if (DEBUG_ENGINES) System.out.println(getEngineName() + ": Done searching.");
    }

    @Override
    public void stopSearch() {
        // Do nothing
    }

    @Override
    public String getMove() {
        if (nextMove == null) {
            List<Move> moves = MoveGenerator.generateLegalMoves(boardState);
            Collections.shuffle(moves);
            nextMove = moves.getFirst();
        }
        return Utils.moveToUci(nextMove);
    }

    @Override
    public void clear() {
        // Does nothing
    }

    @Override
    public String getEngineName() {
        return "Random Move Engine (V1)";
    }

    @Override
    public String toString() {
        return getEngineName();
    }

    @Override
    public void printBoard() {
        System.out.println(boardState.toString());
    }

    public static void main(String[] args) throws IOException {
        new UciConnector(new EngineV1()).loop();
    }
}
