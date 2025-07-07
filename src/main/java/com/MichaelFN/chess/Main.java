package com.MichaelFN.chess;

import com.MichaelFN.chess.arena.Arena;
import com.MichaelFN.chess.common.UciConnector;
import com.MichaelFN.chess.gui.GUI;
import com.MichaelFN.chess.v1.BoardState;
import com.MichaelFN.chess.v6.EngineV6;

import javax.swing.*;

import java.io.IOException;

import static com.MichaelFN.chess.common.Constants.*;

public class Main {
    public static void main(String[] args) throws IOException {
        //Arena arena = new Arena(new BoardState());
        //arena.runTournament(2, 100);
        //arena.runOneVsAll(5, 5, 100);
        //arena.playOneVsOne(VERSION_5, VERSION_6, 5, 200);

        SwingUtilities.invokeLater(() -> new GUI(new BoardState()));
    }
}