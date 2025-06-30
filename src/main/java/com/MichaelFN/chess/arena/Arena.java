package com.MichaelFN.chess.arena;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.MichaelFN.chess.common.Constants.ALL_ENGINES;

public class Arena {
    public static int[][] runTournament(int n_positions, int engineSearchTimeMS) {
        int n_engines = ALL_ENGINES.length;
        int[][] tournamentResult = new int[n_engines][n_engines];

        MatchManager manager = new MatchManager();
        List<String> equalPositions;
        try {
            equalPositions = readChessPositions(n_positions);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (equalPositions == null) {
            System.out.println("Problem reading positions in arena.");
            return null;
        }

        int n_matches = n_engines * n_engines * equalPositions.size();
        int matchCounter = 1;
        for (int i = 0; i < n_engines; i++) {
            for (int j = 0; j < n_engines; j++) {
                for (String initialPosition : equalPositions) {
                    System.out.println("Match " + matchCounter++ + " / " + n_matches + ":");
                    MatchResult matchResult = manager.playMatch(ALL_ENGINES[i], ALL_ENGINES[j], engineSearchTimeMS, initialPosition);
                    if (matchResult == MatchResult.DRAW) {
                        tournamentResult[i][j] += 5;
                        tournamentResult[j][i] += 5;
                    } else if (matchResult == MatchResult.WHITE_WIN) {
                        tournamentResult[i][j] += 10;
                    } else if (matchResult == MatchResult.BLACK_WIN) {
                        tournamentResult[j][i] += 10;
                    }
                }
            }
        }

        return tournamentResult;
    }

    private static List<String> readChessPositions(int n) throws IOException {
        Path path = Paths.get("src/main/resources/100ChessPositions.csv");

        List<String> allLines = Files.readAllLines(path);
        if (allLines.isEmpty()) {
            System.out.println("CSV is empty!");
            return null;
        }

        // Read header to find FEN column index
        String header = allLines.get(0);
        String[] headerParts = header.split(",");
        int fenIndex = Arrays.asList(headerParts).indexOf("FEN");

        if (fenIndex == -1) {
            System.out.println("FEN column not found!");
            return null;
        }

        // Read FEN strings into a list
        List<String> FENStrings = new ArrayList<>();
        for (int i = 1; i < n+1; i++) {
            String[] parts = allLines.get(i).split(",");
            if (parts.length > fenIndex) {
                FENStrings.add(parts[fenIndex].trim());
            }
        }

        return FENStrings;
    }

    public static void printTournamentResults(int[][] result, String[] engineNames) {
        int n = result.length;

        System.out.printf("%-12s", ""); // Top-left corner empty
        for (int i = 0; i < n; i++) {
            System.out.printf("%-12s", engineNames[i]);
        }
        System.out.println();

        for (int i = 0; i < n; i++) {
            System.out.printf("%-12s", engineNames[i]); // Row label
            for (int j = 0; j < n; j++) {
                System.out.printf("%-12d", result[i][j]);
            }
            System.out.println();
        }

        // Optional: print total scores
        System.out.println("\n=== Total Scores ===");
        for (int i = 0; i < n; i++) {
            int total = 0;
            for (int j = 0; j < n; j++) {
                total += result[i][j];
            }
            System.out.printf("%-12s: %d points%n", engineNames[i], total);
        }
    }
}
