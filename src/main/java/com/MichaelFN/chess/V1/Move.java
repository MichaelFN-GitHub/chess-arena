package com.MichaelFN.chess.V1;

import java.util.Objects;

public class Move {
    private final int fromRow;
    private final int fromCol;
    private final int toRow;
    private final int toCol;

    private final Piece movedPiece;

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
        move.setPromotionPiece(new Piece(promotionPieceType, movedPiece.color()));
        return move;
    }

    public static Move createPromotionCapture(int fromRow, int fromCol, int toRow, int toCol, Piece movedPiece, Piece capturedPiece, PieceType promotionPieceType) {
        Move move = createPromotionMove(fromRow, fromCol, toRow, toCol, movedPiece, promotionPieceType);
        move.setCapture(true);
        move.setCapturedPiece(capturedPiece);
        return move;
    }

    public static Move createEnPassantCapture(int fromRow, int fromCol, int toRow, int toCol, Piece movedPiece) {
        Piece capturedPiece = new Piece(PieceType.PAWN, movedPiece.color() == Color.WHITE ? Color.BLACK : Color.WHITE);
        Move move = createCapture(fromRow, fromCol, toRow, toCol, movedPiece, capturedPiece);
        move.setEnPassant(true);
        return move;
    }

    public static Move createCastleKingSide(int fromRow, int fromCol, int toRow, int toCol, Piece movedPiece) {
        Move move = createQuietMove(fromRow, fromCol, toRow, toCol, movedPiece);
        move.setCastleKingside(true);
        return move;
    }

    public static Move createCastleQueenSide(int fromRow, int fromCol, int toRow, int toCol, Piece movedPiece) {
        Move move = createQuietMove(fromRow, fromCol, toRow, toCol, movedPiece);
        move.setCastleQueenside(true);
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Move move = (Move) obj;
        return (fromRow == move.getFromRow() && fromCol == move.getFromCol() && toRow == move.getToRow() && toCol == move.getToCol());
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromRow, fromCol, toRow, toCol);
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
