package com.MichaelFN.chess;

import com.MichaelFN.chess.V1.BoardState;
import com.MichaelFN.chess.V1.Move;
import com.MichaelFN.chess.V1.MoveGenerator;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        BoardState boardState = new BoardState();
        System.out.println(boardState);

        List<Move> moveList = MoveGenerator.generatePseudoLegalMoves(boardState);
        moveList.forEach(System.out::println);
        System.out.println("Total amount of moves: " + moveList.size());
    }
}