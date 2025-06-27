package com.MichaelFN.chess.GUI;

import com.MichaelFN.chess.V1.BoardState;

import javax.swing.*;
import java.awt.*;

public class GUI extends JPanel {
    private JFrame frame;
    private BoardPanel boardPanel;
    private ButtonPanel buttonPanel;

    public GUI(BoardState boardState) {
        boardPanel = new BoardPanel(boardState);
        buttonPanel = new ButtonPanel(boardPanel);

        // Create JFrame
        frame = new JFrame("Chess Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(boardPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.EAST);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
