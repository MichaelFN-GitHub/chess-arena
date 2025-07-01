package v1;

import com.MichaelFN.chess.v1.BoardState;
import com.MichaelFN.chess.v1.Move;
import com.MichaelFN.chess.v1.MoveGenerator;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class PerftTest {

    @Test
    public void testMoveGeneration() throws IOException {
        BoardState boardState = new BoardState();

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("perftsuite.epd");
        if (inputStream == null) {
            throw new IllegalArgumentException("EPD of perft positions not found in resources!");
        }

        List<String> perftSuite;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            perftSuite = reader.lines().toList();
        }

        for (String perftTest : perftSuite) {
            String[] sections = perftTest.split(";");
            String FEN = sections[0];
            boardState.parseFEN(FEN);
            for (int i = 1; i < sections.length; i++) {
                String[] test = sections[i].split(" ");
                int depth = Integer.parseInt(test[0].substring(1));
                long expectedNodes = Long.parseLong(test[1]);
                if (expectedNodes > 500000) break;
                long computedNodes = perft(boardState, depth);
                assertEquals(expectedNodes, computedNodes, "Perft test failed on depth " + depth + " in the following position: " + FEN);
            }
        }
    }

    private long perft(BoardState boardState, int depth) {
        if (depth == 0) {
            return 1;
        }

        long nodes = 0;
        List<Move> moves = MoveGenerator.generateLegalMoves(boardState);
        for (Move move : moves) {
            boardState.makeMove(move);
            nodes += perft(boardState, depth - 1);
            boardState.unmakeMove();
        }

        return nodes;
    }
}
