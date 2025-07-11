package com.MichaelFN.chess.v3;

public class EvaluationConstants {
    public static final int[] PIECE_MATERIAL_VALUE = {100, 320, 330, 500, 900, 0};
    public static final int[][][] PIECE_POSITIONAL_VALUE = {
            // PAWN
            {
                    { 0,  0,  0,  0,  0,  0,  0,  0},
                    {50, 50, 50, 50, 50, 50, 50, 50},
                    {10, 10, 20, 30, 30, 20, 10, 10},
                    { 5,  5, 10, 25, 25, 10,  5,  5},
                    { 0,  0,  0, 20, 20,  0,  0,  0},
                    { 5, -5,-10,  0,  0,-10, -5,  5},
                    { 5, 10, 10,-20,-20, 10, 10,  5},
                    { 0,  0,  0,  0,  0,  0,  0,  0}
            },
            // KNIGHT
            {
                    {-50,-40,-30,-30,-30,-30,-40,-50},
                    {-40,-20,  0,  5,  5,  0,-20,-40},
                    {-30,  5, 10, 15, 15, 10,  5,-30},
                    {-30,  0, 15, 20, 20, 15,  0,-30},
                    {-30,  5, 15, 20, 20, 15,  5,-30},
                    {-30,  0, 10, 15, 15, 10,  0,-30},
                    {-40,-20,  0,  0,  0,  0,-20,-40},
                    {-50,-40,-30,-30,-30,-30,-40,-50}
            },
            // BISHOP
            {
                    {-20,-10,-10,-10,-10,-10,-10,-20},
                    {-10,  5,  0,  0,  0,  0,  5,-10},
                    {-10, 10, 10, 10, 10, 10, 10,-10},
                    {-10,  0, 10, 10, 10, 10,  0,-10},
                    {-10,  5,  5, 10, 10,  5,  5,-10},
                    {-10,  0,  5, 10, 10,  5,  0,-10},
                    {-10,  0,  0,  0,  0,  0,  0,-10},
                    {-20,-10,-10,-10,-10,-10,-10,-20}
            },
            // ROOK
            {
                    { 0,  0,  0,  0,  0,  0,  0,  0},
                    { 5, 10, 10, 10, 10, 10, 10,  5},
                    {-5,  0,  0,  0,  0,  0,  0, -5},
                    {-5,  0,  0,  0,  0,  0,  0, -5},
                    {-5,  0,  0,  0,  0,  0,  0, -5},
                    {-5,  0,  0,  0,  0,  0,  0, -5},
                    {-5,  0,  0,  0,  0,  0,  0, -5},
                    { 0,  0,  0,  5,  5,  0,  0,  0}
            },
            // QUEEN
            {
                    {-20,-10,-10, -5, -5,-10,-10,-20},
                    {-10,  0,  0,  0,  0,  0,  0,-10},
                    {-10,  0,  5,  5,  5,  5,  0,-10},
                    { -5,  0,  5,  5,  5,  5,  0, -5},
                    {  0,  0,  5,  5,  5,  5,  0, -5},
                    {-10,  5,  5,  5,  5,  5,  0,-10},
                    {-10,  0,  5,  0,  0,  0,  0,-10},
                    {-20,-10,-10, -5, -5,-10,-10,-20}
            },
            // KING
            {
                    {-30,-40,-40,-50,-50,-40,-40,-30},
                    {-30,-40,-40,-50,-50,-40,-40,-30},
                    {-30,-40,-40,-50,-50,-40,-40,-30},
                    {-30,-40,-40,-50,-50,-40,-40,-30},
                    {-20,-30,-30,-40,-40,-30,-30,-20},
                    {-10,-20,-20,-20,-20,-20,-20,-10},
                    { 20, 20,  0,  0,  0,  0, 20, 20},
                    { 20, 30, 10,  0,  0, 10, 30, 20}
            }
    };
}
