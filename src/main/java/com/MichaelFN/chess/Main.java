package com.MichaelFN.chess;

import com.MichaelFN.chess.arena.Arena;
import com.MichaelFN.chess.gui.GUI;
import com.MichaelFN.chess.v1.*;
import com.MichaelFN.chess.v5.Board;

import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        //Arena.runTournament(10, 300);
        //SwingUtilities.invokeLater(() -> new GUI(new BoardState()));

        Board board = new Board();
        System.out.println(board.playerToMove);
        System.out.println(board.castlingRights);
        System.out.println(board.enPassantSquare);
        System.out.println(board.halfmoveClock);
        System.out.println(board.fullmoveNumber);
    }
}