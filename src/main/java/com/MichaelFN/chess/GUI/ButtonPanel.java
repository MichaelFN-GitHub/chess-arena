package com.MichaelFN.chess.GUI;

import com.MichaelFN.chess.Interfaces.EngineInterface;

import javax.swing.*;
import java.awt.*;

public class ButtonPanel extends JPanel {

    public ButtonPanel(BoardPanel boardPanel) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.DARK_GRAY);

        // Buttons
        JButton newGameButton = createStyledButton("New Game");
        JButton undoButton = createStyledButton("Undo");
        JButton engineMoveButton = createStyledButton("Engine Move");

        newGameButton.addActionListener(e -> boardPanel.resetBoard());
        undoButton.addActionListener(e -> boardPanel.unmakeMove());
        engineMoveButton.addActionListener(e -> boardPanel.makeEngineMove());


        // Dropdown
        JComboBox<EngineInterface> whiteEngineDropdown = new JComboBox<>(BoardPanel.ALL_ENGINES);
        styleDropdown(whiteEngineDropdown);
        whiteEngineDropdown.addActionListener(e -> {
            EngineInterface selectedEngine = (EngineInterface) whiteEngineDropdown.getSelectedItem();
            boardPanel.setWhiteEngine(selectedEngine);
        });

        JComboBox<EngineInterface> blackEngineDropdown = new JComboBox<>(BoardPanel.ALL_ENGINES);
        styleDropdown(blackEngineDropdown);
        blackEngineDropdown.addActionListener(e -> {
            EngineInterface selectedEngine = (EngineInterface) blackEngineDropdown.getSelectedItem();
            boardPanel.setBlackEngine(selectedEngine);
        });


        // Labels
        JLabel whiteLabel = createStyledLabel("White Engine:");
        JLabel blackLabel = createStyledLabel("Black Engine:");


        // Layout
        add(newGameButton);
        add(undoButton);

        add(Box.createVerticalStrut(50));
        add(whiteLabel);
        add(whiteEngineDropdown);

        add(Box.createVerticalStrut(20));
        add(blackLabel);
        add(blackEngineDropdown);

        add(Box.createVerticalStrut(20));
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

    private void styleDropdown(JComboBox<EngineInterface> comboBox) {
        comboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        comboBox.setMaximumSize(new Dimension(160, 30));
        comboBox.setPreferredSize(new Dimension(160, 30));
        comboBox.setBackground(new Color(60, 60, 60));
        comboBox.setForeground(Color.WHITE);
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        return label;
    }
}
