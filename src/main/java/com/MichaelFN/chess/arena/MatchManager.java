package com.MichaelFN.chess.arena;

import com.MichaelFN.chess.interfaces.Engine;
import com.MichaelFN.chess.v1.*;

import java.util.Stack;

public class MatchManager {

    private BoardState boardState;
    private Stack<String> moveHistory;
    private String initialPositionFEN;

    public MatchManager() {
        this.boardState = new BoardState();
        this.moveHistory = new Stack<>();
        this.initialPositionFEN = boardState.generateFenString();
    }

    public MatchResult playMatch(Engine white, Engine black, int engineSearchTimeMS) {
        reset();

        white.clear();
        black.clear();

        System.out.print("Match between " + white + " and " + black);
        while (!boardState.isGameOver()) {
            Engine engineToMove = boardState.getPlayerToMove() == Color.WHITE ? white : black;
            makeEngineMove(engineToMove, engineSearchTimeMS);
        }

        System.out.print(". Winner: ");
        if (boardState.isCheckmate()) {
            MatchResult result;
            if (boardState.getPlayerToMove() == Color.WHITE) {
                result = MatchResult.BLACK_WIN;
                System.out.println("Black");
            } else {
                result = MatchResult.WHITE_WIN;
                System.out.println("White");
            }
            return result;
        }
        System.out.println("Draw");
        return MatchResult.DRAW;
    }

    public MatchResult playMatch(Engine white, Engine black, int engineSearchTimeMS, String initialPositionFEN) {
        this.initialPositionFEN = initialPositionFEN;
        return playMatch(white, black, engineSearchTimeMS);
    }

    public void makeEngineMove(Engine engine, int engineSearchTimeMS) {
        engine.setPosition(initialPositionFEN, moveHistory);
        engine.startSearch(engineSearchTimeMS);
        String uciMove = engine.getMove();
        Move move = Utils.uciToMove(uciMove, boardState);

        if (!boardState.isLegalMove(move)) return;

        boardState.makeMove(move);
        moveHistory.push(uciMove);
    }

    public void reset() {
        boardState.parseFEN(initialPositionFEN);
        moveHistory.clear();
    }

}
