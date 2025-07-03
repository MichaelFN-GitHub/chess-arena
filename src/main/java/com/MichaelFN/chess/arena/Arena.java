package com.MichaelFN.chess.arena;

import com.MichaelFN.chess.v1.BoardState;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.MichaelFN.chess.common.Constants.ALL_ENGINES;

public class Arena {
    private MatchManager manager;

    public Arena(BoardState boardState) {
        manager = new MatchManager(boardState);
    }

    public int[][][] runTournament(int n_positions, int engineSearchTimeMS) {
        int n_engines = ALL_ENGINES.length;

        int[][] whitePoints = new int[n_engines][n_engines];
        int[][] blackPoints = new int[n_engines][n_engines];

        String[] engineNames = collectEngineNames(n_engines);

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

        int n_matches = n_engines * n_engines * n_positions;
        int matchCounter = 1;

        for (int i = 0; i < n_engines; i++) {
            for (int j = 0; j < n_engines; j++) {
                for (String initialPosition : equalPositions) {
                    System.out.print("Match " + matchCounter++ + " / " + n_matches + ":  ");
                    MatchResult matchResult = manager.playMatch(ALL_ENGINES[i], ALL_ENGINES[j], engineSearchTimeMS, initialPosition);

                    // Update points properly
                    if (matchResult == MatchResult.DRAW) {
                        whitePoints[i][j] += 5;
                        blackPoints[i][j] += 5;
                    } else if (matchResult == MatchResult.WHITE_WIN) {
                        whitePoints[i][j] += 10;
                    } else if (matchResult == MatchResult.BLACK_WIN) {
                        blackPoints[i][j] += 10;
                    }
                }
            }
        }

        printResults(whitePoints, blackPoints, engineNames);

        return new int[][][] {whitePoints, blackPoints};
    }

    public int[][][] runOneVsAll(int versionNumber, int n_positions, int engineSearchTimeMS) {
        int n_engines = ALL_ENGINES.length;

        int[][] whitePoints = new int[n_engines][n_engines];
        int[][] blackPoints = new int[n_engines][n_engines];

        String[] engineNames = collectEngineNames(n_engines);

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

        n_positions = equalPositions.size();
        int n_matches = n_engines * n_positions * 2 - n_positions;
        int matchCounter = 1;

        for (int i = 0; i < n_engines; i++) {
            for (String initialPosition : equalPositions) {

                System.out.print("Match " + matchCounter++ + " / " + n_matches + ":  ");
                playMatchAndSaveResult(whitePoints, blackPoints, versionNumber - 1, i, engineSearchTimeMS, initialPosition);

                if (i == versionNumber - 1) continue;

                // Switch sides
                System.out.print("Match " + matchCounter++ + " / " + n_matches + ":  ");
                playMatchAndSaveResult(whitePoints, blackPoints, i, versionNumber - 1, engineSearchTimeMS, initialPosition);
            }
        }

        printResults(whitePoints, blackPoints, engineNames);

        return new int[][][] {whitePoints, blackPoints};
    }

    private void playMatchAndSaveResult(int[][] whitePoints, int[][] blackPoints,
                                               int whiteEngineIdx, int blackEngineIdx,
                                               int engineSearchTimeMS, String initialPosition) {
        MatchResult matchResult = manager.playMatch(ALL_ENGINES[whiteEngineIdx], ALL_ENGINES[blackEngineIdx], engineSearchTimeMS, initialPosition);

        if (matchResult == MatchResult.DRAW) {
            whitePoints[whiteEngineIdx][blackEngineIdx] += 5;
            blackPoints[whiteEngineIdx][blackEngineIdx] += 5;
        } else if (matchResult == MatchResult.WHITE_WIN) {
            whitePoints[whiteEngineIdx][blackEngineIdx] += 10;
        } else if (matchResult == MatchResult.BLACK_WIN) {
            blackPoints[whiteEngineIdx][blackEngineIdx] += 10;
        }
    }

    private List<String> readChessPositions(int n) throws IOException {
        Path path = Paths.get("src/main/resources/100ChessPositions.csv");

        List<String> allLines = Files.readAllLines(path);
        if (allLines.isEmpty()) {
            System.out.println("CSV is empty!");
            return null;
        }

        String header = allLines.get(0);
        String[] headerParts = header.split(",");
        int fenIndex = Arrays.asList(headerParts).indexOf("FEN");

        if (fenIndex == -1) {
            System.out.println("FEN column not found!");
            return null;
        }

        List<String> FENStrings = new ArrayList<>();
        for (int i = 1; i < n + 1; i++) {
            String[] parts = allLines.get(i).split(",");
            if (parts.length > fenIndex) {
                FENStrings.add(parts[fenIndex].trim());
            }
        }
        return FENStrings;
    }

    public void printResults(int[][] whitePoints, int[][] blackPoints, String[] engineNames) {
        int n = whitePoints.length;

        System.out.printf("%-12s", "");
        for (int i = 0; i < n; i++) {
            System.out.printf("%-12s", engineNames[i]);
        }
        System.out.println();

        // Print White points matrix
        System.out.println("\nPoints as White:");
        for (int i = 0; i < n; i++) {
            System.out.printf("%-12s", engineNames[i]);
            for (int j = 0; j < n; j++) {
                System.out.printf("%-12d", whitePoints[i][j]);
            }
            System.out.println();
        }

        // Print Black points matrix
        System.out.println("\nPoints as Black:");
        for (int i = 0; i < n; i++) {
            System.out.printf("%-12s", engineNames[i]);
            for (int j = 0; j < n; j++) {
                System.out.printf("%-12d", blackPoints[j][i]);
            }
            System.out.println();
        }

        // Print totals: points as White, as Black, and combined total
        System.out.println("\n=== Total Scores ===");
        System.out.printf("%-12s %-12s %-12s %-12s%n", "Engine", "As White", "As Black", "Total");
        for (int i = 0; i < n; i++) {
            int pointsAsWhite = 0;
            int pointsAsBlack = 0;
            for (int j = 0; j < n; j++) {
                pointsAsWhite += whitePoints[i][j];
                pointsAsBlack += blackPoints[j][i];
            }
            int total = pointsAsWhite + pointsAsBlack;
            System.out.printf("%-12s %-12d %-12d %-12d%n", engineNames[i], pointsAsWhite, pointsAsBlack, total);
        }
    }

    private String[] collectEngineNames(int n_engines) {
        String[] engineNames = new String[n_engines];
        for (int i = 0; i < n_engines; i++) {
            String[] nameSections = ALL_ENGINES[i].toString().split(" ");
            engineNames[i] = nameSections[nameSections.length - 1];
        }
        return engineNames;
    }
}
