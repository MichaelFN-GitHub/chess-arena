package com.MichaelFN.chess.Interfaces;

/**
 * Defines the core methods for a chess engine interface.
 * This interface abstracts the communication and control layer between the engine logic and the GUI.
 */
public interface EngineInterface {

    /**
     * Initialize or reset the engine state.
     * Called at the start or to reset between games.
     */
    void initialize();

    /**
     * Sets the current position on the board.
     * The position can be specified as a FEN string or as moves from the start position.
     *
     * @param FEN specified position in FEN format.
     */
    void setPosition(String FEN);

    /**
     * Starts the search for the best move from the current position.
     *
     * @param timeLimitMillis Time allowed for the search in milliseconds.
     */
    void startSearch(long timeLimitMillis);

    /**
     * Gets the best move found by the engine after a search.
     *
     * @return The best move in UCI notation.
     */
    String getBestMove();
}

