package V1;

import com.MichaelFN.chess.V1.BoardState;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FENTest {

    @Test
    public void testFenParsingAndGeneration() throws IOException {
        BoardState boardState = new BoardState();

        List<String> fenStrings;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(
                this.getClass()
                        .getClassLoader()
                        .getResourceAsStream("fen_test_positions.txt"))))) {
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
