package com.MichaelFN.chess.GUI;

import com.MichaelFN.chess.V1.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class BoardPanel extends JPanel implements MouseListener, MouseMotionListener {
    private static final int TILE_SIZE = 80;
    private static final int BOARD_SIZE = 8;
    private static final int ENGINE_SEARCH_TIME_MS = 1000;

    private final BoardState boardState;
    private final EngineV1 V1;
    private final Image[][] pieceImages;
    private GameStatus gameStatus;

    private int draggedFromRow, draggedFromCol;
    private int mouseX, mouseY;
    private boolean dragging;
    private Piece draggedPiece;

    public BoardPanel(BoardState boardState) {
        this.boardState = boardState;
        this.V1 = new EngineV1();
        this.pieceImages = new Image[2][6];
        this.gameStatus = boardState.evaluateGameStatus();

        V1.initialize();
        loadPieceImages();

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
        if (dragging) highlightLegalMoves(g);
        drawPieces(g);
        if (gameStatus.isGameOver()) drawGameStatus(g);
    }

    private void highlightLegalMoves(Graphics g) {
        List<Move> legalMoves = MoveGenerator.generateLegalMoves(boardState);

        g.setColor(new Color(255, 255, 0, 100));
        for (Move move : legalMoves) {
            int row = move.getFromRow();
            int col = move.getFromCol();
            if (row == draggedFromRow && col == draggedFromCol) {
                g.fillRect(move.getToCol() * TILE_SIZE, move.getToRow() * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
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
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Piece piece = boardState.getPiece(row, col);

                // Do not draw dragged piece
                if (piece != null && !(dragging && row == draggedFromRow && col == draggedFromCol)) {
                    Image img = getPieceImage(piece);
                    int x = col * TILE_SIZE;
                    int y = row * TILE_SIZE;
                    g.drawImage(img, x, y, TILE_SIZE, TILE_SIZE, null);
                }
            }
        }

        // Draw dragged piece on top
        if (dragging && draggedPiece != null) {
            Image img = getPieceImage(draggedPiece);
            int x = mouseX - TILE_SIZE/2;
            int y = mouseY - TILE_SIZE/2;
            g.drawImage(img, x, y, TILE_SIZE, TILE_SIZE, null);
        }
    }

    public void drawGameStatus(Graphics g) {
        if (gameStatus.isGameOver()) {
            Graphics2D g2 = (Graphics2D) g.create();

            // Semi-transparent dark overlay
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Message box
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 32));
            String msg = gameStatus.getGameStatus();
            FontMetrics fm = g2.getFontMetrics();
            int msgWidth = fm.stringWidth(msg);
            int x = (getWidth() - msgWidth) / 2;
            int y = getHeight() / 2;

            g2.drawString(msg, x, y);

            g2.dispose();
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
        gameStatus = boardState.evaluateGameStatus();
        repaint();
    }

    public void unmakeMove() {
        boardState.unmakeMove();
        gameStatus = boardState.evaluateGameStatus();
        repaint();
    }

    public void makeEngineMove() {
        if (gameStatus.isGameOver()) return;

        V1.setPosition(boardState.generateFenString());
        V1.startSearch(ENGINE_SEARCH_TIME_MS);
        String uciMove = V1.getBestMove();
        Move move = Utils.uciToMove(uciMove, boardState);
        makeMoveIfLegal(move);
        repaint();
    }

    private void makeMoveIfLegal(Move move) {
        if (gameStatus.isGameOver()) return;

        if (boardState.isLegalMove(move)) {
            boardState.makeMove(move);
            gameStatus = boardState.evaluateGameStatus();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int col = e.getX() / TILE_SIZE;
        int row = e.getY() / TILE_SIZE;
        Piece piece = boardState.getPiece(row, col);

        if (piece != null) {
            draggedFromRow = row;
            draggedFromCol = col;
            draggedPiece = piece;
            dragging = true;
            mouseX = e.getX();
            mouseY = e.getY();
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (dragging) {
            mouseX = e.getX();
            mouseY = e.getY();
            repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (dragging) {
            int toCol = e.getX() / TILE_SIZE;
            int toRow = e.getY() / TILE_SIZE;
            String uciMove = Utils.createUciMove(draggedFromRow, draggedFromCol, toRow, toCol, draggedPiece);
            dragging = false;
            draggedPiece = null;

            Move move = Utils.uciToMove(uciMove, boardState);
            makeMoveIfLegal(move);

            setCursor(Cursor.getDefaultCursor());
            repaint();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
