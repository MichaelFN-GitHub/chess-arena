package com.MichaelFN.chess;

import com.MichaelFN.chess.V1.BoardState;
import com.MichaelFN.chess.V1.Move;
import com.MichaelFN.chess.V1.MoveGenerator;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        BoardState boardState = new BoardState();
        boardState.parseFEN("8/8/8/8/8/K7/P7/k7 w - - 0 1");
        System.out.println(boardState);

        List<Move> moves = MoveGenerator.generateLegalMoves(boardState);
        moves.forEach(System.out::println);
        System.out.println("Total amount of moves: " + moves.size());
    }
}