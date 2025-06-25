package com.MichaelFN.chess.V1;

public class Move {
    private int fromRow;
    private int fromCol;
    private int toRow;
    private int toCol;

    private Piece movedPiece;

    private boolean isCapture;
    private Piece capturedPiece;
    private boolean isPromotion;
    private Piece promotionPiece;
    private boolean isEnPassant;
    private boolean isCastleKingside;
    private boolean isCastleQueenside;
    private boolean isDoublePawnPush;

    public Move(int fromRow, int fromCol, int toRow, int toCol, Piece movedPiece) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
        this.movedPiece = movedPiece;
    }

    public static Move createQuietMove(int fromRow, int fromCol, int toRow, int toCol, Piece movedPiece) {
        return new Move(fromRow, fromCol, toRow, toCol, movedPiece);
    }

    public static Move createDoublePawnPush(int fromRow, int fromCol, int toRow, int toCol, Piece movedPiece) {
        Move move = createQuietMove(fromRow, fromCol, toRow, toCol, movedPiece);
        move.setDoublePawnPush(true);
        return move;
    }

    public static Move createCapture(int fromRow, int fromCol, int toRow, int toCol, Piece movedPiece, Piece capturedPiece) {
        Move move = createQuietMove(fromRow, fromCol, toRow, toCol, movedPiece);
        move.setCapture(true);
        move.setCapturedPiece(capturedPiece);
        return move;
    }

    public static Move createPromotionMove(int fromRow, int fromCol, int toRow, int toCol, Piece movedPiece, PieceType promotionPieceType) {
        Move move = createQuietMove(fromRow, fromCol, toRow, toCol, movedPiece);
        move.setPromotion(true);
        move.setPromotionPiece(new Piece(promotionPieceType, movedPiece.getColor()));
        return move;
    }

    public static Move createPromotionCapture(int fromRow, int fromCol, int toRow, int toCol, Piece movedPiece, Piece capturedPiece, PieceType promotionPieceType) {
        Move move = createPromotionMove(fromRow, fromCol, toRow, toCol, movedPiece, promotionPieceType);
        move.setCapture(true);
        move.setCapturedPiece(capturedPiece);
        return move;
    }

    @Override
    public String toString() {
        return movedPiece + ": " + Utils.coordsToSquareString(fromRow, fromCol) + Utils.coordsToSquareString(toRow, toCol) +
                (isCapture ? " (capture)" : "") +
                (isPromotion ? " promote to " + promotionPiece : "") +
                (isEnPassant ? " (en passant)" : "") +
                (isCastleKingside ? " (O-O)" : "") +
                (isCastleQueenside ? " (O-O-O)" : "");
    }

    public int getFromRow() {
        return fromRow;
    }

    public int getFromCol() {
        return fromCol;
    }

    public int getToRow() {
        return toRow;
    }

    public int getToCol() {
        return toCol;
    }

    public Piece getMovedPiece() {
        return movedPiece;
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    public void setCapturedPiece(Piece capturedPiece) {
        this.capturedPiece = capturedPiece;
    }

    public boolean isCapture() {
        return isCapture;
    }

    public void setCapture(boolean capture) {
        isCapture = capture;
    }

    public boolean isPromotion() {
        return isPromotion;
    }

    public void setPromotion(boolean promotion) {
        isPromotion = promotion;
    }

    public Piece getPromotionPiece() {
        return promotionPiece;
    }

    public void setPromotionPiece(Piece promotionPiece) {
        this.promotionPiece = promotionPiece;
    }

    public boolean isEnPassant() {
        return isEnPassant;
    }

    public void setEnPassant(boolean enPassant) {
        isEnPassant = enPassant;
    }

    public boolean isCastleKingside() {
        return isCastleKingside;
    }

    public void setCastleKingside(boolean castleKingside) {
        isCastleKingside = castleKingside;
    }

    public boolean isCastleQueenside() {
        return isCastleQueenside;
    }

    public void setCastleQueenside(boolean castleQueenside) {
        isCastleQueenside = castleQueenside;
    }

    public boolean isDoublePawnPush() {
        return isDoublePawnPush;
    }

    public void setDoublePawnPush(boolean doublePawnPush) {
        isDoublePawnPush = doublePawnPush;
    }
}
