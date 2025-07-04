package com.MichaelFN.chess.v5.search;

import java.util.Arrays;

public class TranspositionTable {

    private static final int APPROX_ENTRY_SIZE_BYTES = 30;

    private final int size;
    private final TTEntry[] table;
    private int collisions = 0;

    public TranspositionTable(int sizeInMB) {
        // Allocate sizeInMB megabytes
        int entries = (sizeInMB * 1024 * 1024) / APPROX_ENTRY_SIZE_BYTES;

        // MAke size a power of 2 for efficient indexing
        size = Integer.highestOneBit(entries);
        table = new TTEntry[size];
    }

    public void put(long key, int depth, int score, int flag) {
        put(key, depth, score, flag, 0);
    }

    public void put(long key, int depth, int score, int flag, int bestMove) {
        int idx = (int) (key & (size - 1));
        TTEntry entry = table[idx];

        if (entry != null) {
            if (entry.key != key) collisions++;

            // Only replace if new depth is greater or equal
            if (entry.key == key || entry.depth <= depth) {
                entry.update(key, depth, score, flag, bestMove);
            }
        } else {
            table[idx] = new TTEntry(key, depth, score, flag, bestMove);
        }
    }

    public int getScore(long key) {
        TTEntry entry = get(key);
        return entry != null ? entry.score : Integer.MIN_VALUE;
    }

    public TTEntry get(long key) {
        int idx = (int) (key & (size - 1));
        TTEntry entry = table[idx];
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
