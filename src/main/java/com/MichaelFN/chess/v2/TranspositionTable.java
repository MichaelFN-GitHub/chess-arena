package com.MichaelFN.chess.v2;

import java.util.HashMap;
import java.util.Map;

public class TranspositionTable {
    public static class Entry {
        public int depth;
        public int score;
        public int flag;

        public static final int EXACT = 0;
        public static final int LOWERBOUND = 1;
        public static final int UPPERBOUND = 2;

        public Entry(int depth, int score, int flag) {
            this.depth = depth;
            this.score = score;
            this.flag = flag;
        }
    }

    private final Map<Long, Entry> table = new HashMap<>();

    public void put(long key, Entry entry) {
        table.put(key, entry);
    }

    public Entry get(long key) {
        return table.get(key);
    }
}
