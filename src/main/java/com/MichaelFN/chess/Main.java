package com.MichaelFN.chess;

import com.MichaelFN.chess.arena.Arena;
import com.MichaelFN.chess.gui.GUI;
import com.MichaelFN.chess.v1.*;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        //String[] engineNames = {"EngineV1", "EngineV2", "EngineV3", "EngineV4"};
        //int[][] result = Arena.runTournament(1, 100);
        //Arena.printTournamentResults(result, engineNames);

        SwingUtilities.invokeLater(() -> new GUI(new BoardState()));
    }
}