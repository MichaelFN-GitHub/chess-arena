package com.MichaelFN.chess.v2;

import com.MichaelFN.chess.v1.Move;

import java.util.Arrays;

public class TranspositionTable {

    private static final int APPROX_ENTRY_SIZE_BYTES = 40;

    private final int size;
    private final Entry[] table;
    private int collisions = 0;

    // TTEntry used only internally
    static class Entry {
        long key;
        int depth;
        int score;
        int flag;
        Move bestMove;

        static final int EXACT = 0;
        static final int LOWERBOUND = 1;
        static final int UPPERBOUND = 2;

        Entry(long key, int depth, int score, int flag, Move bestMove) {
            this.key = key;
            this.depth = depth;
            this.score = score;
            this.flag = flag;
            this.bestMove = bestMove;
        }

        void update(long key, int depth, int score, int flag, Move bestMove) {
            this.key = key;
            this.depth = depth;
            this.score = score;
            this.flag = flag;
            this.bestMove = bestMove;
        }
    }

    public TranspositionTable(int sizeInMB) {
        // Allocate sizeInMB megabytes
        int entries = (sizeInMB * 1024 * 1024) / APPROX_ENTRY_SIZE_BYTES;

        // MAke size a power of 2 for efficient indexing
        size = Integer.highestOneBit(entries);
        table = new Entry[size];
    }

    private int index(long key) {
        // Use lower bits of key for indexing
        // Note: Collisions could happen if size of table is too small...
        return (int) (key & (size - 1));
    }

    public void put(long key, int depth, int score, int flag) {
        put(key, depth, score, flag, null);
    }

    public void put(long key, int depth, int score, int flag, Move bestMove) {
        int idx = index(key);
        Entry entry = table[idx];

        if (entry != null) {
            if (entry.key != key) collisions++;

            // Only replace if new depth is greater or equal
            if (entry.key == key || entry.depth <= depth) {
                entry.update(key, depth, score, flag, bestMove);
            }
        } else {
            table[idx] = new Entry(key, depth, score, flag, bestMove);
        }
    }

    public int getScore(long key) {
        Entry entry = get(key);
        return entry != null ? entry.score : Integer.MIN_VALUE;
    }

    public Entry get(long key) {
        int idx = index(key);
        Entry entry = table[idx];
        if (entry != null && entry.key == key) {
            return entry;
        }
        return null;
    }

    public int getCollisions() {
        return collisions;
    }

    public void clear() {
        Arrays.fill(table, null);
        collisions = 0;
    }

    @Override
    public String toString() {
        return "TranspositionTable: size=" + size + ", collisions=" + collisions + "";
    }
}
