package com.MichaelFN.chess.GUI;

import com.MichaelFN.chess.V1.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.io.IOException;
import java.util.Objects;

public class BoardPanel extends JPanel {
    private static final int TILE_SIZE = 80;
    private static final int BOARD_SIZE = 8;
    private static final int ENGINE_SEARCH_TIME_MS = 1000;

    private BoardState boardState;
    private EngineV1 V1;
    private Image[][] pieceImages;

    public BoardPanel(BoardState boardState) {
        this.boardState = boardState;
        this.V1 = new EngineV1();
        this.pieceImages = new Image[2][6];

        V1.initialize();
        loadPieceImages();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
        drawPieces(g);
    }

    private void drawBoard(Graphics g) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                boolean isLight = (row + col) % 2 == 0;
                g.setColor(isLight ? Color.WHITE : Color.GRAY);
                g.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    private void drawPieces(Graphics g) {
        Piece[][] position = boardState.getPosition();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Piece piece = position[row][col];
                if (piece != null) {
                    Image img = getPieceImage(piece);
                    int x = col * TILE_SIZE;
                    int y = row * TILE_SIZE;
                    g.drawImage(img, x, y, TILE_SIZE, TILE_SIZE, null);
                }
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(TILE_SIZE * BOARD_SIZE, TILE_SIZE * BOARD_SIZE);
    }

    private void loadPieceImages() {
        String[] colors = {"l", "d"};
        String[] pieces = {"p", "n", "b", "r", "q", "k"};
        for (int color = 0; color < 2; color++) {
            for (int piece = 0; piece < 6; piece++) {
                String filename = "/images/Chess_" + pieces[piece] + colors[color] + "t45.png";
                try {
                    pieceImages[color][piece] = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(filename)));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private Image getPieceImage(Piece piece) {
        return pieceImages[piece.getColor().ordinal()][piece.getType().ordinal()];
    }

    public void resetBoard() {
        boardState.reset();
        repaint();
    }

    public void unmakeMove() {
        boardState.unmakeMove();
        repaint();
    }

    public void makeEngineMove() {
        V1.setPosition(boardState.generateFenString());
        V1.startSearch(ENGINE_SEARCH_TIME_MS);
        String uciMove = V1.getBestMove();
        Move move = Utils.uciToMove(uciMove, boardState);
        boardState.makeMove(move);
        repaint();
    }
}
