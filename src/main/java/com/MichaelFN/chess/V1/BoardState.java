package com.MichaelFN.chess.V1;

import java.util.List;
import java.util.Stack;

public class BoardState {
    private final BoardInitializer boardInitializer;

    // Chess rule variables
    private Piece[][] position;
    private Color playerToMove;
    private boolean[][] castlingRights;
    private int[] enPassantSquare;
    private int halfmoveClock;
    private int fullmoveNumber;

    // History variables
    private final Stack<Move> moveHistory;
    private final Stack<boolean[][]> castlingRightsHistory;
    private final Stack<int[]> enPassantSquareHistory;
    private final Stack<Integer> halfmoveClockHistory;
    private final Stack<Integer> fullmoveNumberHistory;

    // Helpers
    private int[][] kingPositions;

    public BoardState() {
        this.boardInitializer = new BoardInitializer(this);
        reset();

        moveHistory = new Stack<>();
        castlingRightsHistory = new Stack<>();
        enPassantSquareHistory = new Stack<>();
        halfmoveClockHistory = new Stack<>();
        fullmoveNumberHistory = new Stack<>();
    }

    public boolean isLegalMove(Move move) {
        List<Move> legalMoves = MoveGenerator.generateLegalMoves(this);
        for (Move legalMove : legalMoves) {
            if (move.equals(legalMove)) return true;
        }
        return false;
    }

    public void makeMove(Move move) {
        int fromRow = move.getFromRow();
        int fromCol = move.getFromCol();
        int toRow = move.getToRow();
        int toCol = move.getToCol();
        Piece movedPiece = move.getMovedPiece();
        Piece capturedPiece = move.getCapturedPiece();

        // Update history
        moveHistory.push(move);
        castlingRightsHistory.push(new boolean[][] {
                { castlingRights[0][0],castlingRights[0][1] },
                { castlingRights[1][0], castlingRights[1][1]}
        });
        enPassantSquareHistory.push(enPassantSquare == null ? null : new int[]{enPassantSquare[0], enPassantSquare[1]});
        halfmoveClockHistory.push(halfmoveClock);
        fullmoveNumberHistory.push(fullmoveNumber);

        // Move piece
        position[fromRow][fromCol] = null;
        position[toRow][toCol] = movedPiece;

        // En passant
        if (move.isEnPassant()) {
            int rowBehindPawn = toRow + (movedPiece.color() == Color.WHITE ? 1 : -1);
            position[rowBehindPawn][toCol] = null;
        }

        // Double pawn push
        if (move.isDoublePawnPush()) {
            int rowBehindPawn = toRow + (movedPiece.color() == Color.WHITE ? 1 : -1);
            enPassantSquare = new int[]{rowBehindPawn, toCol};
        } else {
            enPassantSquare = null;
        }

        // Promotion
        if (move.isPromotion()) {
            Piece promotionPiece = move.getPromotionPiece();
            position[toRow][toCol] = promotionPiece;
        }

        // Castling
        if (move.isCastleKingside()) {

            // Move rook to left of king
            int rookFromCol = toCol + 1;
            int rookToCol = toCol - 1;
            Piece rook = position[toRow][rookFromCol];
            position[toRow][rookFromCol] = null;
            position[toRow][rookToCol] = rook;

        } else if (move.isCastleQueenside()) {

            // Move rook to right of king
            int rookFromCol = toCol - 2;
            int rookToCol = toCol + 1;
            Piece rook = position[toRow][rookFromCol];
            position[toRow][rookFromCol] = null;
            position[toRow][rookToCol] = rook;
        }

        // Castling rights and king position
        if (movedPiece.type() == PieceType.KING) {
            int colorIndex = movedPiece.color().ordinal();
            castlingRights[colorIndex][0] = false;
            castlingRights[colorIndex][1] = false;
            kingPositions[colorIndex][0] = toRow;
            kingPositions[colorIndex][1] = toCol;
        } else if (movedPiece.type() == PieceType.ROOK) {
            int colorIndex = movedPiece.color().ordinal();
            if (fromCol == 7) {
                castlingRights[colorIndex][0] = false;
            } else if (fromCol == 0) {
                castlingRights[colorIndex][1] = false;
            }
        } else if (move.isCapture() && capturedPiece.type() == PieceType.ROOK) {
            int colorIndex = 1 - movedPiece.color().ordinal();
            if (toCol == 7) {
                castlingRights[colorIndex][0] = false;
            } else if (toCol == 0) {
                castlingRights[colorIndex][1] = false;
            }
        }

        if (move.isCapture() || movedPiece.type() == PieceType.PAWN) halfmoveClock = 0;
        else halfmoveClock++;

        if (movedPiece.color() == Color.BLACK) fullmoveNumber++;

        playerToMove = playerToMove == Color.WHITE ? Color.BLACK : Color.WHITE;
    }

    public void unmakeMove() {
        if (moveHistory.isEmpty()) {
            System.out.println("Unable to unmake any more moves.");
            return;
        }

        Move move = moveHistory.pop();
        int fromRow = move.getFromRow();
        int fromCol = move.getFromCol();
        int toRow = move.getToRow();
        int toCol = move.getToCol();
        Piece movedPiece = move.getMovedPiece();
        Piece capturedPiece = move.getCapturedPiece();

        // Move piece
        position[fromRow][fromCol] = movedPiece;
        position[toRow][toCol] = capturedPiece;

        // King position
        if (movedPiece.type() == PieceType.KING) {
            int colorIndex = movedPiece.color().ordinal();
            kingPositions[colorIndex][0] = fromRow;
            kingPositions[colorIndex][1] = fromCol;
        }

        // En passant
        if (move.isEnPassant()) {
            int rowBehindPawn = toRow + (movedPiece.color() == Color.WHITE ? 1 : -1);
            position[toRow][toCol] = null;
            position[rowBehindPawn][toCol] = capturedPiece;
        }

        // Castling (move rook to starting square)
        if (move.isCastleKingside()) {
            int rookFromCol = toCol + 1;
            int rookToCol = toCol - 1;
            Piece rook = position[toRow][rookToCol];
            position[toRow][rookFromCol] = rook;
            position[toRow][rookToCol] = null;

        } else if (move.isCastleQueenside()) {
            int rookFromCol = toCol - 2;
            int rookToCol = toCol + 1;
            Piece rook = position[toRow][rookToCol];
            position[toRow][rookFromCol] = rook;
            position[toRow][rookToCol] = null;
        }

        // Update history
        castlingRights = castlingRightsHistory.pop();
        enPassantSquare = enPassantSquareHistory.pop();
        halfmoveClock = halfmoveClockHistory.pop();
        fullmoveNumber = fullmoveNumberHistory.pop();

        playerToMove = playerToMove == Color.WHITE ? Color.BLACK : Color.WHITE;
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
                    String pieceString = Utils.pieceToString(piece);
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
                String pieceString = Utils.pieceToString(piece);
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

    public void reset() {
        boardInitializer.initializeStartingPosition();
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

    public int[][] getKingPositions() {
        return kingPositions;
    }

    public void setKingPositions(int[][] kingPositions) {
        this.kingPositions = kingPositions;
    }

    public int[] getKingPosition(Color color) {
        return kingPositions[color.ordinal()];
    }

    public Piece getPiece(int row, int col) {
        return position[row][col];
    }
}
