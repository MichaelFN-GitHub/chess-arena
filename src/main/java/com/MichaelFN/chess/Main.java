package com.MichaelFN.chess;

import com.MichaelFN.chess.GUI.GUI;
import com.MichaelFN.chess.V1.*;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUI(new BoardState()));
    }
}