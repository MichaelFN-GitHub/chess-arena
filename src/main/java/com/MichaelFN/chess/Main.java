package com.MichaelFN.chess;

import com.MichaelFN.chess.V1.*;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        BoardState boardState = new BoardState();
        EngineV1 V1 = new EngineV1();
        V1.initialize();
        for (int i = 0; i < 1000; i++) {
            System.out.println(boardState);
            String FEN = boardState.generateFenString();

            V1.setPosition(FEN);
            V1.startSearch(1000);
            String uciMove = V1.getBestMove();

            System.out.println("Before UCI move:");
            Move move = Utils.uciToMove(uciMove, boardState);
            System.out.println("UCI move: " + uciMove);
            System.out.println("Move: " + move);
            boardState.makeMove(move);
        }
    }
}