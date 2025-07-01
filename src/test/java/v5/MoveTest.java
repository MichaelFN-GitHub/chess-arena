package v5;

import static com.MichaelFN.chess.v5.Bitboard.*;
import static com.MichaelFN.chess.v5.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

import com.MichaelFN.chess.v5.Move;
import org.junit.jupiter.api.Test;

public class MoveTest {

    @Test
    public void testQuietMove() {
        int move = Move.createQuietMove(C2, C4);
        assertEquals(C2, Move.getFrom(move));
        assertEquals(C4, Move.getTo(move));
        assertTrue(Move.isQuiet(move));
        assertFalse(Move.isCapture(move));
        assertFalse(Move.isPromotion(move));
        assertFalse(Move.isDoublePawnPush(move));
        assertFalse(Move.isCastleKingSide(move));
        assertFalse(Move.isCastleQueenSide(move));
        assertFalse(Move.isEnPassant(move));
        assertEquals("c2c4", Move.toString(move));
    }

    @Test
    public void testCaptureMove() {
        int move = Move.createCapture(E3, D4, PAWN);
        assertTrue(Move.isCapture(move));
        assertFalse(Move.isPromotion(move));
        assertEquals(E3, Move.getFrom(move));
        assertEquals(D4, Move.getTo(move));
        assertEquals("e3d4 (captures P)", Move.toString(move));
    }

    @Test
    public void testDoublePawnPush() {
        int move = Move.createDoublePawnPush(E2, E4);
        assertTrue(Move.isDoublePawnPush(move));
        assertFalse(Move.isCapture(move));
        assertEquals("e2e4 (double pawn push)", Move.toString(move));
    }

    @Test
    public void testCastleKingSide() {
        int move = Move.createCastleKingSide(E1, G1);
        assertTrue(Move.isCastleKingSide(move));
        assertFalse(Move.isCapture(move));
        assertEquals("e1g1 (O-O)", Move.toString(move));
    }

    @Test
    public void testCastleQueenSide() {
        int move = Move.createCastleQueenSide(E1, C1);
        assertTrue(Move.isCastleQueenSide(move));
        assertFalse(Move.isCapture(move));
        assertEquals("e1c1 (O-O-O)", Move.toString(move));
    }

    @Test
    public void testEnPassantCapture() {
        int move = Move.createEnPassantCapture(E5, D6);
        assertTrue(Move.isCapture(move));
        assertTrue(Move.isEnPassant(move));
        assertEquals("e5d6 (captures P) (en passant)", Move.toString(move));
    }

    @Test
    public void testPromotionMove() {
        int move = Move.createPromotionMove(E7, E8, QUEEN);
        assertTrue(Move.isPromotion(move));
        assertFalse(Move.isCapture(move));
        assertEquals(QUEEN, Move.getPromotionPiece(move));
        assertEquals("e7e8=Q", Move.toString(move));

        move = Move.createPromotionMove(A7, A8, KNIGHT);
        assertTrue(Move.isPromotion(move));
        assertFalse(Move.isCapture(move));
        assertEquals(KNIGHT, Move.getPromotionPiece(move));
        assertEquals("a7a8=N", Move.toString(move));
    }

    @Test
    public void testPromotionCaptureMove() {
        int move = Move.createPromotionCapture(E7, E8, QUEEN, BISHOP);
        assertTrue(Move.isCapture(move));
        assertTrue(Move.isPromotion(move));
        assertEquals(BISHOP, Move.getCapturedPiece(move));
        assertEquals(QUEEN, Move.getPromotionPiece(move));
        assertEquals("e7e8=Q (captures B)", Move.toString(move));
    }
}
