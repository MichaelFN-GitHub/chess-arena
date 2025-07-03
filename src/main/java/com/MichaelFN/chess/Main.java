package com.MichaelFN.chess;

import com.MichaelFN.chess.gui.GUI;
import com.MichaelFN.chess.v1.BoardState;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        //Arena.runTournament(10, 300);
        SwingUtilities.invokeLater(() -> new GUI(new BoardState()));
    }
}