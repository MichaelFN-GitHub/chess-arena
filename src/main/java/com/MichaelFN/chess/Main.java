package com.MichaelFN.chess;

import com.MichaelFN.chess.V1.BoardState;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        BoardState boardState = new BoardState();
        System.out.println(boardState);
        System.out.println(boardState.getPlayerToMove());
        System.out.println(Arrays.toString(boardState.getEnPassantSquare()));
        System.out.println(Arrays.deepToString(boardState.getCastlingRights()));
        System.out.println(boardState.getHalfmoveClock());
        System.out.println(boardState.getFullmoveNumber());
    }
}