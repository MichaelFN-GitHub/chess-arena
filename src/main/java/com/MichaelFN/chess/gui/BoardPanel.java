package com.MichaelFN.chess.gui;

import com.MichaelFN.chess.interfaces.Engine;
import com.MichaelFN.chess.v1.*;
import com.MichaelFN.chess.v5.Constants;

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
import java.util.Stack;

import static com.MichaelFN.chess.common.Constants.ALL_ENGINES;
import static com.MichaelFN.chess.common.Constants.DEBUG_GUI;

public class BoardPanel extends JPanel implements MouseListener, MouseMotionListener {
    private static final int TILE_SIZE = 80;
    private static final int BOARD_SIZE = 8;
    private static final int ENGINE_SEARCH_TIME_MS = 1000;

    private final BoardState boardState;
    private final Image[][] pieceImages;

    private int draggedFromRow, draggedFromCol;
    private int mouseX, mouseY;
    private boolean dragging;
    private Piece draggedPiece;

    private Engine whiteEngine = ALL_ENGINES[0];
    private Engine blackEngine = ALL_ENGINES[0];

    private final String FEN;
    private final Stack<String> moveHistory;

    public BoardPanel(BoardState boardState) {
        this.boardState = boardState;
        boardState.parseFEN("rnb1kbnr/ppp1pppp/8/4q3/8/2N5/PPPP1PPP/R1BQKBNR w KQkq - 2 4");
        this.pieceImages = new Image[2][6];
        this.FEN = boardState.generateFenString();
        this.moveHistory = new Stack<>();
        String moves = "g1e2 c7c5 d2d3 b8c6 c1f4 e5f5 f4e3 e7e5 c3e4 c5c4 e2g3 f5g6 d3c4 c8g4 f1e2 a8d8 e3d2 g4e2 d1e2 c6d4 e2d3 f7f5 e4g5 h7h6 g5f3 e5e4 d3d4 d8d4 f3d4 g8e7 e1g1 h6h5 h2h4 g6g4 d2g5 e4e3 g3e2 e3f2 f1f2 e7g6 d4f5 g4c4 e2d4 f8c5 b2b3 c4g8 a1e1 e8d7 c2c3 a7a5 e1e6 g8h7 e6e3 h8f8 a2a4 d7c8 f2f1 h7g8 c3c4 c8b8 e3e4 g8f7 g5e3 c5e7 d4e6 f8h8 f5g7 f7g8 g7f5 e7h4 f5h4 g6h4 f1f8 g8f8 e6f8 h4f3 g1f2 h8f8 g2f3 f8f5 e3b6 b8c8 e4d4 f5f8 b6a5 f8h8 d4d5 h5h4 d5c5 c8d7 c5c7 d7e6 c7b7 h4h3 b7b6 e6d7 b6b7 d7d6 a5b4 d6c6 b7b5 h3h2 b5c5 c6d7 c5d5 d7e6 d5d1 h2h1q d1h1 h8h1 b4d2 h1d1 f2e2 d1b1 b3b4 b1b2 b4b5 e6d6 a4a5 b2b3 a5a6 b3b2 e2d3 b2a2 d2e3 a2a3 d3e4 a3a4 a6a7 a4c4 e4f5 c4a4 b5b6 d6c6 e3d4 a4a3 f3f4 a3a2 f5e4 a2e2 d4e3 e2a2 f4f5 a2a1 f5f6 c6b7 f6f7 a1f1 e3f4 f1e1 e4f5 e1b1 f7f8q b1b5 f5g4 b5a5 f8b8 b7c6 b8c7 c6b5 b6b7 a5a6";
        for (String m : moves.split(" ")) {
            boardState.makeMove(Utils.uciToMove(m, boardState));
            moveHistory.push(m);
        }

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
        if (boardState.isGameOver()) drawGameStatus(g);
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
        Graphics2D g2 = (Graphics2D) g.create();

        // Semi-transparent dark overlay
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Message box
        GameStatus gameStatus = boardState.getGameStatus();
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 32));
        String msg = gameStatus.getGameStatusMessage();
        FontMetrics fm = g2.getFontMetrics();
        int msgWidth = fm.stringWidth(msg);
        int x = (getWidth() - msgWidth) / 2;
        int y = getHeight() / 2;

        g2.drawString(msg, x, y);
        g2.dispose();

        System.out.println("Winner: " + gameStatus.getWinnerString());
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
        return pieceImages[piece.color().ordinal()][piece.type().ordinal()];
    }

    public void resetBoard() {
        boardState.reset();
        moveHistory.clear();
        repaint();
    }

    public void unmakeMove() {
        boardState.unmakeMove();
        if (!moveHistory.empty()) moveHistory.pop();
        repaint();
    }

    public void makeEngineMove() {
        if (boardState.isGameOver()) return;

        Engine engine = boardState.getPlayerToMove() == com.MichaelFN.chess.v1.Color.WHITE ? whiteEngine : blackEngine;

        engine.setPosition(FEN, moveHistory);
        engine.startSearch(Constants.MAX_SEARCH_DEPTH, ENGINE_SEARCH_TIME_MS);
        String uciMove = engine.getMove();
        Move move = Utils.uciToMove(uciMove, boardState);
        makeMoveIfLegal(move);
        repaint();
    }

    private void makeMoveIfLegal(Move move) {
        if (boardState.isGameOver()) return;

        if (MoveGenerator.generateLegalMoves(boardState).contains(move)) {
            boardState.makeMove(move);
            moveHistory.push(Utils.moveToUci(move));
        }

        if (DEBUG_GUI) System.out.println("Current position: " + boardState.generateFenString());;
    }

    public void setWhiteEngine(Engine engine) {
        this.whiteEngine = engine;
    }

    public void setBlackEngine(Engine engine) {
        this.blackEngine = engine;
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
