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

    public String generateFenString() {
        StringBuilder FEN = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int counter = 0;
            for (int j = 0; j < 8; j++) {
                Piece piece = position[i][j];
                if (piece != null) {
                    if (counter > 0) {
                        FEN.append(counter);
                        counter = 0;
                    }
                    String pieceString = Utils.pieceToStringMap.get(piece);
                    FEN.append(pieceString);
                } else {
                    counter++;
                }
            }
            if (counter > 0) {
                FEN.append(counter);
            }
            if (i < 7) {
                FEN.append("/");
            }
        }

        FEN.append(playerToMove == Color.WHITE ? " w " : " b ");
        if (castlingRights[0][0]) FEN.append("K");
        if (castlingRights[0][1]) FEN.append("Q");
        if (castlingRights[1][0]) FEN.append("k");
        if (castlingRights[1][1]) FEN.append("q");
        if (FEN.charAt(FEN.length() - 1) == ' ') FEN.append("-");
        FEN.append(" ").append(enPassantSquare == null ? "-" : Utils.coordsToSquareString(enPassantSquare));
        FEN.append(" ").append(halfmoveClock);
        FEN.append(" ").append(fullmoveNumber);
        return FEN.toString();
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
        stringBuilder.append("+---+---+---+---+---+---+---+---+\n  a   b   c   d   e   f   g   h\n\n");
        stringBuilder.append(generateFenString());
        return stringBuilder.toString();
    }

    public void parseFEN(String FEN) {
        boardInitializer.initializeFromFen(FEN);
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
