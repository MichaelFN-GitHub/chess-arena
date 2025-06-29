package com.MichaelFN.chess.gui;

import com.MichaelFN.chess.v1.BoardState;

import javax.swing.*;
import java.awt.*;

public class GUI extends JPanel {

    public GUI(BoardState boardState) {
        BoardPanel boardPanel = new BoardPanel(boardState);
        ButtonPanel buttonPanel = new ButtonPanel(boardPanel);

        // Create JFrame
        JFrame frame = new JFrame("Chess Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(boardPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.EAST);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
