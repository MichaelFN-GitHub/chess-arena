package com.MichaelFN.chess;

import com.MichaelFN.chess.common.Perft;
import com.MichaelFN.chess.v5.Zobrist;
import com.MichaelFN.chess.v5.board.Bitboard;
import com.MichaelFN.chess.v5.board.Board;
import com.MichaelFN.chess.v5.move.Move;
import com.MichaelFN.chess.v5.move.MoveGenerator;

public class Main {
    public static void main(String[] args) {
        //Arena.runTournament(10, 300);
        //SwingUtilities.invokeLater(() -> new GUI(new BoardState()));

        Board board = new Board();
        //board.parseFEN("4k3/8/8/8/8/8/8/4K2R w K - 0 1");
        System.out.println(board);
        System.out.println(board.hashKey);

        MoveGenerator mg = new MoveGenerator();
        mg.generateLegalMoves(board, 0);
        int[] moves = mg.legalMoves[0];
        int n_moves = mg.legalMoveCounts[0];
        System.out.println(n_moves);
        board.makeMove(moves[0]);
        board.unmakeMove();
        System.out.println(board.repetitionCount);

        System.out.println(Zobrist.computeHash(board));
        System.out.println(board.hashKey);

        int depth = 5;
        System.out.println(Perft.perft(board, depth));
        //System.out.println(Perft.bulkPerft(board, depth));
        //System.out.println(Perft.perftDebug(board, depth));

        System.out.println(Zobrist.computeHash(board));
        System.out.println(board.hashKey);
    }
}