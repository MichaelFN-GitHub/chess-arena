package com.MichaelFN.chess;

import com.MichaelFN.chess.v5.board.Bitboard;
import com.MichaelFN.chess.v5.board.Board;
import com.MichaelFN.chess.v5.move.MoveGenerator;
import com.MichaelFN.chess.v5.move.MoveTables;

import static com.MichaelFN.chess.v5.board.Bitboard.*;

public class Main {
    public static void main(String[] args) {
        //Arena.runTournament(10, 300);
        //SwingUtilities.invokeLater(() -> new GUI(new BoardState()));

        Board board = new Board();
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 7; j++) {
                Bitboard.printBitboard(board.pieces[i][j]);
            }
        }
        System.out.println(board.playerToMove);
        System.out.println(board.castlingRights);
        System.out.println(board.enPassantSquare);
        System.out.println(board.halfmoveClock);
        System.out.println(board.fullmoveNumber);

        System.out.println();
        MoveGenerator.generatePseudoLegalMoves(board);
        System.out.println(MoveGenerator.moveCounter);
    }
}