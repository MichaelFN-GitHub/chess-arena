package v2;

import com.MichaelFN.chess.v1.BoardState;
import org.junit.Test;

public class ZobristTest {
    @Test
    public void testZobristHashing() {
        BoardState boardState = new BoardState();

        // Initialize starting position and compute hash key
        String FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        boardState.parseFEN(FEN);
        long hashKey = boardState.getKey();

        // Change piece position
        boardState.parseFEN("rnbqkbnr/ppppppp1/7p/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        assert (hashKey != boardState.getKey());

        // Remove piece
        boardState.parseFEN("rnbqkbnr/pppppp1p/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        assert (hashKey != boardState.getKey());

        // Change player
        boardState.parseFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 1");
        assert (hashKey != boardState.getKey());

        // Change castling rights
        boardState.parseFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w Qkq - 0 1");
        assert (hashKey != boardState.getKey());
        boardState.parseFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w Kkq - 0 1");
        assert (hashKey != boardState.getKey());
        boardState.parseFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQq - 0 1");
        assert (hashKey != boardState.getKey());
        boardState.parseFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQk - 0 1");
        assert (hashKey != boardState.getKey());
        boardState.parseFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 0 1");
        assert (hashKey != boardState.getKey());

        // Change en passant square
        boardState.parseFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq e6 0 1");
        assert (hashKey != boardState.getKey());
    }

    @Test
    public void testMoveHashing() {

    }
}
