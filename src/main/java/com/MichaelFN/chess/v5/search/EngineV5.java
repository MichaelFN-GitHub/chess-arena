package com.MichaelFN.chess.v5.search;

import com.MichaelFN.chess.interfaces.Engine;
import com.MichaelFN.chess.v5.Constants;
import com.MichaelFN.chess.v5.Utils;
import com.MichaelFN.chess.v5.board.Board;

import java.util.Stack;

import static com.MichaelFN.chess.common.Constants.DEBUG_ENGINES;

public class EngineV5 implements Engine {
    protected Board board;
    protected int bestMove;

    private final Evaluator evaluator = new Evaluator();
    private final Searcher searcher = new Searcher(evaluator);

    public EngineV5() {
        initialize();
    }

    @Override
    public void initialize() {
        board = new Board();
        if (DEBUG_ENGINES) System.out.println(getEngineName() + ": Initialized.");
    }

    @Override
    public void setPosition(String FEN, Stack<String> uciMoves) {
        board.parseFEN(FEN);
        for (String move : uciMoves) {
            board.makeMove(Utils.uciToMove(move, board));
        }
        if (DEBUG_ENGINES) System.out.println(getEngineName() + ": Position has been set.");
    }

    @Override
    public void startSearch(long timeLimitMillis) {
        if (DEBUG_ENGINES) System.out.println(getEngineName() + ": Search started...");
        bestMove = searcher.negamax(board, Constants.MAX_PLY - 1, timeLimitMillis);
        if (DEBUG_ENGINES) System.out.println(getEngineName() + ": Done searching.");
    }

    @Override
    public String getMove() {
        return Utils.moveToUci(bestMove, board);
    }

    @Override
    public String getEngineName() {
        return "Pesto Bitboard Engine (V5)";
    }

    @Override
    public void clear() {
        searcher.clearTranspositionTable();
    }

    @Override
    public String toString() {
        return getEngineName();
    }
}
