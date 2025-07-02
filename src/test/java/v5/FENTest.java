package v5;

import com.MichaelFN.chess.v5.board.Board;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class FENTest {

    @Test
    public void testFenParsingAndGeneration() throws IOException {
        Board board = new Board();

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("fen_test_positions.txt");
        if (inputStream == null) {
            throw new IllegalArgumentException("FEN test positions not found in resources!");
        }

        List<String> fenStrings;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            fenStrings = reader.lines().toList();
        }

        for (String FEN : fenStrings) {
            try {
                board.parseFEN(FEN);
                String generatedFEN = board.generateFENString();
                assertEquals(FEN, generatedFEN);
            } catch (Exception e) {
                fail("Exception caught during FEN comparison");
            }
        }
    }
}
