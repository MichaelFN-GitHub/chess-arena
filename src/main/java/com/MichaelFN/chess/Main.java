package com.MichaelFN.chess;

import com.MichaelFN.chess.arena.Arena;
import com.MichaelFN.chess.gui.GUI;
import com.MichaelFN.chess.v1.BoardState;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        Arena arena = new Arena(new BoardState());

        //arena.runTournament(2, 100);
        arena.runOneVsAll(5, 5, 100);

        SwingUtilities.invokeLater(() -> new GUI(new BoardState()));
    }
}