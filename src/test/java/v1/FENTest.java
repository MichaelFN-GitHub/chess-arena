package v1;

import com.MichaelFN.chess.v1.BoardState;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FENTest {

    @Test
    public void testFenParsingAndGeneration() throws IOException {
        BoardState boardState = new BoardState();

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
                boardState.parseFEN(FEN);
                String generatedFEN = boardState.generateFenString();
                assertEquals(FEN, generatedFEN);
            } catch (Exception e) {
                fail("Exception caught during FEN comparison");
            }
        }
    }
}
