package com.MichaelFN.chess.common;

import com.MichaelFN.chess.v1.BoardState;
import com.MichaelFN.chess.v1.Move;
import com.MichaelFN.chess.v5.board.Board;
import com.MichaelFN.chess.v5.move.MoveGenerator;

import java.util.List;

import static com.MichaelFN.chess.v5.Utils.isInCheck;

public class Perft {
    private static final MoveGenerator moveGenerator = new MoveGenerator();

    public static long perft(BoardState boardState, int depth) {
        if (depth == 0) {
            return 1;
        }

        long nodes = 0;
        List<Move> moves = com.MichaelFN.chess.v1.MoveGenerator.generateLegalMoves(boardState);
        for (Move move : moves) {
            boardState.makeMove(move);
            nodes += perft(boardState, depth - 1);
            boardState.unmakeMove();
        }

        return nodes;
    }

    public static long perft(Board board, int depth) {
        return recursivePerft(board, depth, 0);
    }

    private static long recursivePerft(Board board, int depth, int ply) {
        if (depth == 0) {
            return 1;
        }

        long nodes = 0;

        int playerColor = board.playerToMove;

        moveGenerator.generatePseudoLegalMoves(board, ply);
        int[] moves = moveGenerator.pseudoMoves[ply];
        int n_moves = moveGenerator.pseudoMoveCounts[ply];
        for (int i = 0; i < n_moves; i++) {
            int move = moves[i];
            board.makeMove(move);
            if (!isInCheck(board, playerColor)) nodes += recursivePerft(board, depth - 1, ply + 1);
            board.unmakeMove();
        }

        return nodes;
    }

    public static long bulkPerft(Board board, int depth) {
        return recursiveBulkPerft(board, depth, 0);
    }

    private static long recursiveBulkPerft(Board board, int depth, int ply) {
        moveGenerator.generateLegalMoves(board, ply);
        int n_moves = moveGenerator.legalMoveCounts[ply];
        int[] moves = moveGenerator.legalMoves[ply];

        if (depth == 1) {
            return n_moves;
        }

        int playerColor = board.playerToMove;
        long nodes = 0;
        for (int i = 0; i < n_moves; i++) {
            int move = moves[i];
            board.makeMove(move);
            if (!isInCheck(board, playerColor)) nodes += recursiveBulkPerft(board, depth - 1, ply + 1);
            board.unmakeMove();
        }

        return nodes;
    }

    public static long perftDebug(Board board, int depth) {
        return recursivePerftDebug(board, depth, 0);
    }

    private static long recursivePerftDebug(Board board, int depth, int ply) {
        if (depth == 0) {
            return 1;
        }

        moveGenerator.generateLegalMoves(board, ply);
        int n_moves = moveGenerator.legalMoveCounts[ply];
        int[] moves = moveGenerator.legalMoves[ply];

        int playerColor = board.playerToMove;
        long nodes = 0;
        for (int i = 0; i < n_moves; i++) {
            int move = moves[i];
            board.makeMove(move);
            long n = 0;
            if (!isInCheck(board, playerColor)) n = recursivePerftDebug(board, depth - 1, ply + 1);
            board.unmakeMove();

            if (ply == 0) System.out.println(com.MichaelFN.chess.v5.move.Move.toString(move) + ": " + n);

            nodes += n;
        }

        return nodes;
    }
}
