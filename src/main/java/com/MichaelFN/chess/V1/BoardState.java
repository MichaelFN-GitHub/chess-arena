package com.MichaelFN.chess.V1;

public class BoardState {
    private BoardInitializer boardInitializer;
    private Piece[][] position;
    private Color playerToMove;
    private boolean[][] castlingRights;
    private int[] enPassantSquare;
    private int halfmoveClock;
    private int fullmoveNumber;

    public BoardState() {
        this.boardInitializer = new BoardInitializer(this);
        boardInitializer.initializeStartingPosition();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");
        for (int i = 0; i < 8; i++) {
            stringBuilder.append("+---+---+---+---+---+---+---+---+\n| ");
            for (int j = 0; j < 8; j++) {
                Piece piece = position[i][j];
                String pieceString = Utils.pieceToStringMap.get(piece);
                stringBuilder.append(pieceString).append(" | ");
            }
            stringBuilder.append(8-i).append("\n");
        }
        stringBuilder.append("+---+---+---+---+---+---+---+---+\n  a   b   c   d   e   f   g   h\n");
        return stringBuilder.toString();
    }

    public Piece[][] getPosition() {
        return position;
    }

    public void setPosition(Piece[][] position) {
        this.position = position;
    }

    public Color getPlayerToMove() {
        return playerToMove;
    }

    public void setPlayerToMove(Color playerToMove) {
        this.playerToMove = playerToMove;
    }

    public boolean[][] getCastlingRights() {
        return castlingRights;
    }

    public void setCastlingRights(boolean[][] castlingRights) {
        this.castlingRights = castlingRights;
    }

    public int[] getEnPassantSquare() {
        return enPassantSquare;
    }

    public void setEnPassantSquare(int[] enPassantSquare) {
        this.enPassantSquare = enPassantSquare;
    }

    public int getHalfmoveClock() {
        return halfmoveClock;
    }

    public void setHalfmoveClock(int halfmoveClock) {
        this.halfmoveClock = halfmoveClock;
    }

    public int getFullmoveNumber() {
        return fullmoveNumber;
    }

    public void setFullmoveNumber(int fullmoveNumber) {
        this.fullmoveNumber = fullmoveNumber;
    }
}
