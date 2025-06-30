package com.MichaelFN.chess.interfaces;

import java.util.Stack;

/**
 * Defines the core methods for a chess engine interface.
 * This interface abstracts the communication and control layer between the engine logic and the GUI.
 */
public interface Engine {

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
     * @param moves moves made from initial position in UCI format (so that engine can reconstruct position)
     */
    void setPosition(String FEN, Stack<String> moves);

    /**
     * Starts the search for the move from the current position.
     *
     * @param timeLimitMillis Time allowed for the search in milliseconds.
     */
    void startSearch(long timeLimitMillis);

    /**
     * Gets the move found by the engine after a search.
     *
     * @return The move in UCI notation.
     */
    String getMove();

    /**
     * Gets the name of the engine.
     */
    String getEngineName();

    /**
     * Clears important structures like transposition table used for search.
     */
    void clear();

    String toString();
}

