package com.MichaelFN.chess.v5.search;

public class TTEntry {
    public long key;
    public int depth;
    public int score;
    public int flag;
    public int bestMove;

    public static final int EXACT = 0;
    public static final int LOWERBOUND = 1;
    public static final int UPPERBOUND = 2;

    public TTEntry(long key, int depth, int score, int flag, int bestMove) {
        this.key = key;
        this.depth = depth;
        this.score = score;
        this.flag = flag;
        this.bestMove = bestMove;
    }

    public void update(long key, int depth, int score, int flag, int bestMove) {
        this.key = key;
        this.depth = depth;
        this.score = score;
        this.flag = flag;
        this.bestMove = bestMove;
    }
}
