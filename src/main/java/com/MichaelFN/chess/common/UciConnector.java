package com.MichaelFN.chess.common;

import com.MichaelFN.chess.interfaces.Engine;
import com.MichaelFN.chess.v5.EngineV5;
import com.MichaelFN.chess.v6.EngineV6;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.*;

public class UciConnector {
    private final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private final PrintWriter out = new PrintWriter(System.out, true);
    private final ExecutorService searchExecutor = Executors.newSingleThreadExecutor();
    private Future<?> searchFuture;

    private Engine engine;

    public UciConnector(Engine engine) {
        this.engine = engine;
    }

    public void loop() throws IOException {
        String line;
        while ((line = in.readLine()) != null) {
            String[] tok = line.trim().split("\\s+");
            if (tok.length == 0) continue;
            switch (tok[0]) {
                case "uci":
                    out.println("id name " + engine.getEngineName());
                    out.println("id author MichaelFN");
                    out.println("uciok");
                    break;

                case "isready":
                    out.println("readyok");
                    break;

                case "ucinewgame":
                    engine.clear();
                    break;

                case "position":
                    handlePosition(tok);
                    break;

                case "d":
                    engine.printBoard();
                    break;

                case "go":
                    handleGo(tok);
                    break;

                case "stop":
                    if (searchFuture != null) {
                        searchFuture.cancel(true);
                        engine.stopSearch();
                    }
                    break;

                case "quit":
                    shutdown();
                    return;

                default:
                    // ignore
            }
        }
    }

    private void handlePosition(String[] tok) {
        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

        int i = 1;
        if (i < tok.length && tok[i].equals("fen")) {
            StringBuilder sb = new StringBuilder();
            i++;
            // FEN is 6 space‐separated fields
            for (int f = 0; f < 6 && i < tok.length; f++, i++) {
                sb.append(tok[i]).append(' ');
            }
            fen = sb.toString().trim();
        } else if (i < tok.length && tok[i].equals("startpos")) {
            i++;
        }

        Stack<String> moves = new Stack<>();
        if (i < tok.length && tok[i].equals("moves")) {
            for (i = i+1; i < tok.length; i++) {
                moves.add(tok[i]);
            }
        }

        engine.setPosition(fen, moves);
    }

    private void handleGo(String[] tok) {
        int depth = 64;
        long movetime = 1000;

        for (int i = 1; i < tok.length; i++) {
            if (tok[i].equals("depth") && i+1 < tok.length) {
                depth = Integer.parseInt(tok[++i]);
            }
            if (tok[i].equals("movetime") && i+1 < tok.length) {
                movetime = Integer.parseInt(tok[++i]);
            }
        }

        int finalDepth = depth;
        long finalMovetime = movetime;
        searchFuture = searchExecutor.submit(() -> {
            engine.startSearch(finalDepth, Math.min(1000, finalMovetime / 10));
            String best = engine.getMove();
            out.println("bestmove " + best);
        });
    }

    private void shutdown() {
        searchExecutor.shutdownNow();
    }
}
