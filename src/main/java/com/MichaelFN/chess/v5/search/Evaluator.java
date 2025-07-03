package com.MichaelFN.chess.v5.search;

import com.MichaelFN.chess.common.PestoConstants;
import com.MichaelFN.chess.v5.board.Board;

import static com.MichaelFN.chess.v5.Constants.*;
import static com.MichaelFN.chess.v5.board.Bitboard.*;

public class Evaluator {

    public int evaluate(Board board) {
        int[] mg = new int[2];
        int[] eg = new int[2];
        int phase = 0;

        int playerToMove = board.playerToMove;

        // Loop over every color and piece type
        for (int color = WHITE; color <= BLACK; color++) {
            for (int type = PAWN; type <= KING; type++) {
                int typeIdx = type - 1;

                long pieceBB = board.pieces[color][type];
                int pieceCount = Long.bitCount(pieceBB);

                // Phase
                phase += PestoConstants.GAME_PHASE_VALUES[typeIdx] * pieceCount;

                // Material
                mg[color] += PestoConstants.MG_VALUE[typeIdx] * pieceCount;
                eg[color] += PestoConstants.EG_VALUE[typeIdx] * pieceCount;

                // Loop over every bit to get positional values
                boolean isBlack = color == BLACK;
                while (pieceBB != 0) {
                    int square = lsb(pieceBB);
                    pieceBB = clearLsb(pieceBB);

                    // Mirror square if black
                    if (isBlack) square = mirror(square);
                    mg[color] += PestoConstants.MG_PST[typeIdx][square];
                    eg[color] += PestoConstants.EG_PST[typeIdx][square];
                }
            }
        }

        // Cap phase to 24 (early queen promotion)
        phase = Math.min(phase, 24);

        // Interpolate score between midgame and endgame
        int mgScore = mg[playerToMove] - mg[1 - playerToMove];
        int egScore = eg[playerToMove] - eg[1 - playerToMove];
        return (mgScore * phase + egScore * (24 - phase)) / 24;
    }
}
