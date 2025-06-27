package com.MichaelFN.chess;

import com.MichaelFN.chess.GUI.GUI;
import com.MichaelFN.chess.V1.*;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        BoardState boardState = new BoardState();

        EngineV1 V1 = new EngineV1();

        SwingUtilities.invokeLater(() -> new GUI(boardState));
    }
}