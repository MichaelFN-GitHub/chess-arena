package com.MichaelFN.chess.V1;

import java.util.List;

public class Move {
    private int fromRank;
    private int fromFile;
    private int toRank;
    private int toFile;

    private Piece movedPiece;

    private boolean isCapture;
    private Piece capturedPiece;
    private boolean isPromotion;
    private Piece promotionPiece;
    private boolean isEnPassant;
    private boolean isCastleKingside;
    private boolean isCastleQueenside;
    private boolean isDoublePawnPush;

    public Move(int fromRank, int fromFile, int toRank, int toFile, Piece movedPiece) {
        this.fromRank = fromRank;
        this.fromFile = fromFile;
        this.toRank = toRank;
        this.toFile = toFile;
        this.movedPiece = movedPiece;
    }

    public static Move createQuietMove(int fromRank, int fromFile, int toRank, int toFile, Piece movedPiece) {
        return new Move(fromRank, fromFile, toRank, toFile, movedPiece);
    }

    public static Move createDoublePawnPush(int fromRank, int fromFile, int toRank, int toFile, Piece movedPiece) {
        Move move = createQuietMove(fromRank, fromFile, toRank, toFile, movedPiece);
        move.setDoublePawnPush(true);
        return move;
    }

    public static Move createCapture(int fromRank, int fromFile, int toRank, int toFile, Piece movedPiece, Piece capturedPiece) {
        Move move = createQuietMove(fromRank, fromFile, toRank, toFile, movedPiece);
        move.setCapture(true);
        move.setCapturedPiece(capturedPiece);
        return move;
    }

    @Override
    public String toString() {
        return movedPiece + ": " + Utils.coordsToSquareString(fromRank, fromFile) + " -> " + Utils.coordsToSquareString(toRank, toFile) +
                (isCapture ? " (capture)" : "") +
                (isPromotion ? " promote to " + promotionPiece : "") +
                (isEnPassant ? " (en passant)" : "") +
                (isCastleKingside ? " (O-O)" : "") +
                (isCastleQueenside ? " (O-O-O)" : "");
    }

    public int getFromRank() {
        return fromRank;
    }

    public int getFromFile() {
        return fromFile;
    }

    public int getToRank() {
        return toRank;
    }

    public int getToFile() {
        return toFile;
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
