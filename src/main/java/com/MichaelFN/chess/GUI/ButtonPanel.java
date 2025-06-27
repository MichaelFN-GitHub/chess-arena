package com.MichaelFN.chess.GUI;

import javax.swing.*;
import java.awt.*;

public class ButtonPanel extends JPanel {

    public ButtonPanel(BoardPanel boardPanel) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.DARK_GRAY);

        JButton newGameButton = createStyledButton("New Game");
        JButton undoButton = createStyledButton("Undo");
        JButton engineMoveButton = createStyledButton("Engine Move");

        newGameButton.addActionListener(e -> boardPanel.resetBoard());
        undoButton.addActionListener(e -> boardPanel.unmakeMove());
        engineMoveButton.addActionListener(e -> boardPanel.makeEngineMove());

        add(newGameButton);
        add(undoButton);
        add(engineMoveButton);
        add(Box.createVerticalGlue());
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(160, 40));
        button.setPreferredSize(new Dimension(160, 40));
        button.setBackground(new Color(80, 80, 80));
        button.setForeground(Color.WHITE);
        return button;
    }
}
