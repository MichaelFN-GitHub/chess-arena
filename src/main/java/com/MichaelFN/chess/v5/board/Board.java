package com.MichaelFN.chess.v5.board;

import com.MichaelFN.chess.v5.Utils;
import com.MichaelFN.chess.v5.Constants;
import com.MichaelFN.chess.v5.move.Move;

import java.util.Arrays;
import java.util.HashMap;

import static com.MichaelFN.chess.v5.Constants.*;

public class Board {
    /**
     * Bitboards are an efficient representation of a chessboard.
     * Bitboards are 64-bit longs where each bit corresponds to a square on the board.
     * In my case, bit index 0 (LSB) represents a1 and bit index 63 (MSB) represents h8.
     * This is called Little-Endian Rank-File Mapping.
     *
     * Each bitboard encodes the presence of a specific type of piece or a group of pieces.
     * For example, one bitboard may represent all white pawns.
     *
     * Read more: <a href="https://www.chessprogramming.org/Bitboards">...</a>
     */

    // Chess rule variables
    public long[][] pieces;     // Bitboards: pieces[color][pieceType], where type 0 is all pieces
    public int playerToMove;    // 0 = white, 1 = black
    public int castlingRights;  // 1000 = white k-side, 0100 = white q-side, 0010 = black k-side, 0001 = black q-side
    public int enPassantSquare; // -1 = none, 0..63 -> a1..h8
    public int halfmoveClock;   // 0..50
    public int fullmoveNumber;  // 1..MAX_MOVES
    public int gameStatus;      // 0 = ongoing, 1 = white win, 2 = black win, 3 = draw
    public final HashMap<Long,Integer> repetitionCount = new HashMap<>();

    // History variables
    private final int[] moveHistory = new int[MAX_MOVES];
    private final int[] castlingRightsHistory = new int[MAX_MOVES];
    private final int[] enPassantSquareHistory = new int[MAX_MOVES];
    private final int[] halfmoveClockHistory = new int[MAX_MOVES];
    private final int[] fullmoveNumberHistory = new int[MAX_MOVES];
    private final long[] hashKeyHistory = new long[MAX_MOVES];
    private final int[] capturedPieceHistory = new int[MAX_MOVES];

    // Helpers
    public int[] pieceAtSquare = new int[64];
    public int moveCounter = 0;
    public long hashKey;


    public Board() {
        this.pieces = new long[2][7];
        BoardInitializer.initializeBoard(this);
    }

    public void makeMove(int move) {
        int color = playerToMove;
        int enemyColor = 1 - playerToMove;

        int from = Move.getFrom(move);
        int to = Move.getTo(move);
        boolean isEnPassant = Move.isEnPassant(move);

        int allPieces = Constants.ALL_PIECES;
        int nonePiece = Constants.NONE;
        int movedPiece = pieceAtSquare[from];
        int capturedPiece = isEnPassant ? PAWN : pieceAtSquare[to];

        long fromBB = Bitboard.SQUARE_BB_LOOK_UP[from];
        long toBB = Bitboard.SQUARE_BB_LOOK_UP[to];
        long fromToBB = fromBB | toBB;

        // Update history
        moveHistory[moveCounter] = move;
        castlingRightsHistory[moveCounter] = castlingRights;
        enPassantSquareHistory[moveCounter] = enPassantSquare;
        halfmoveClockHistory[moveCounter] = halfmoveClock;
        fullmoveNumberHistory[moveCounter] = fullmoveNumber;
        capturedPieceHistory[moveCounter] = capturedPiece;

        // Move piece
        pieces[color][movedPiece] ^= fromToBB;
        pieces[color][allPieces] ^= fromToBB;
        pieceAtSquare[from] = nonePiece;
        pieceAtSquare[to] = movedPiece;

        // Capture
        if (Move.isCapture(move)) {
            halfmoveClock = 0;

            // En passant
            if (Move.isEnPassant(move)) {
                capturedPiece = PAWN;
                int captureSquare = enPassantSquare + (color == WHITE ? -8 : 8);
                long captureSquareBB = Bitboard.SQUARE_BB_LOOK_UP[captureSquare];
                pieces[enemyColor][capturedPiece] ^= captureSquareBB;
                pieces[enemyColor][ALL_PIECES] ^= captureSquareBB;
                pieceAtSquare[captureSquare] = nonePiece;
            }

            // Normal capture
            else {
                pieces[enemyColor][capturedPiece] ^= toBB;
                pieces[enemyColor][allPieces] ^= toBB;
            }
        }

        // Halfmove clock
        else if (movedPiece == PAWN) halfmoveClock = 0;
        else halfmoveClock++;

        // Double pawn push
        if (Move.isDoublePawnPush(move)) {
            enPassantSquare = color == Constants.WHITE ? to - 8 : to + 8;
        } else {
            enPassantSquare = -1;
        }

        // Promotion
        if (Move.isPromotion(move)) {
            int promotionPiece = Move.getPromotionPiece(move);
            pieces[color][PAWN] ^= toBB;
            pieces[color][promotionPiece] ^= toBB;
            pieceAtSquare[to] = promotionPiece;
        }

        // Castling
        if (Move.isCastleKingSide(move)) {
            int rookFrom = (color == WHITE) ? Bitboard.H1 : Bitboard.H8;
            int rookTo   = (color == WHITE) ? Bitboard.F1 : Bitboard.F8;
            long rookMove = Bitboard.SQUARE_BB_LOOK_UP[rookFrom] | Bitboard.SQUARE_BB_LOOK_UP[rookTo];
            pieces[color][ROOK] ^= rookMove;
            pieces[color][ALL_PIECES] ^= rookMove;
            pieceAtSquare[rookFrom] = nonePiece;
            pieceAtSquare[rookTo] = ROOK;
        } else if (Move.isCastleQueenSide(move)) {
            int rookFrom = (color == WHITE) ? Bitboard.A1 : Bitboard.A8;
            int rookTo   = (color == WHITE) ? Bitboard.D1 : Bitboard.D8;
            long rookMove = Bitboard.SQUARE_BB_LOOK_UP[rookFrom] | Bitboard.SQUARE_BB_LOOK_UP[rookTo];
            pieces[color][ROOK] ^= rookMove;
            pieces[color][ALL_PIECES] ^= rookMove;
            pieceAtSquare[rookFrom] = nonePiece;
            pieceAtSquare[rookTo] = ROOK;
        }

        // Castling rights
        if (castlingRights != 0) {

            // King moved
            if (movedPiece == Constants.KING) {
                castlingRights &= color == WHITE ? ~CASTLE_WHITE : ~CASTLE_BLACK;
            }

            // Rook moved from original square
            else if (movedPiece == Constants.ROOK) {
                if (color == WHITE) {
                    if (from == Bitboard.A1) {
                        castlingRights &= ~CASTLE_WHITE_QUEENSIDE;
                    } else if (from == Bitboard.H1) {
                        castlingRights &= ~CASTLE_WHITE_KINGSIDE;
                    }
                }

                // Black
                else {
                    if (from == Bitboard.A8) {
                        castlingRights &= ~CASTLE_BLACK_QUEENSIDE;
                    } else if (from == Bitboard.H8) {
                        castlingRights &= ~CASTLE_BLACK_KINGSIDE;
                    }
                }
            }

            // Rook was captured on its original square
            if (capturedPiece == Constants.ROOK) {
                if (enemyColor == WHITE) {
                    if (to == Bitboard.A1) castlingRights &= ~CASTLE_WHITE_QUEENSIDE;
                    else if (to == Bitboard.H1) castlingRights &= ~CASTLE_WHITE_KINGSIDE;
                } else {
                    if (to == Bitboard.A8) castlingRights &= ~CASTLE_BLACK_QUEENSIDE;
                    else if (to == Bitboard.H8) castlingRights &= ~CASTLE_BLACK_KINGSIDE;
                }
            }
        }

        // Fullmove number
        if (color == BLACK) fullmoveNumber++;

        // Change player to move
        playerToMove = enemyColor;
        moveCounter++;

        repetitionCount.put(hashKey, repetitionCount.getOrDefault(hashKey, 0) + 1);
    }

    public void unmakeMove() {
        if (moveCounter == 0) {
            System.out.println("Cannot unmake any more moves.");
            return;
        }

        moveCounter--;
        int move = moveHistory[moveCounter];
        int color = 1 - playerToMove;
        int enemyColor = playerToMove;

        int from = Move.getFrom(move);
        int to = Move.getTo(move);

        int movedPiece = pieceAtSquare[to];
        int capturedPiece;

        long fromBB = Bitboard.SQUARE_BB_LOOK_UP[from];
        long toBB = Bitboard.SQUARE_BB_LOOK_UP[to];
        long fromToBB = fromBB | toBB;

        // Restore game state from history
        repetitionCount.put(hashKey, repetitionCount.get(hashKey) - 1);
        castlingRights = castlingRightsHistory[moveCounter];
        enPassantSquare = enPassantSquareHistory[moveCounter];
        halfmoveClock = halfmoveClockHistory[moveCounter];
        fullmoveNumber = fullmoveNumberHistory[moveCounter];
        hashKey = hashKeyHistory[moveCounter];
        playerToMove = color;

        // Promotion
        if (Move.isPromotion(move)) {
            int promotionPiece = Move.getPromotionPiece(move);
            movedPiece = PAWN;
            pieces[color][promotionPiece] ^= toBB;
            pieces[color][PAWN] ^= toBB;
        }

        // Moved piece
        pieces[color][movedPiece] ^= fromToBB;
        pieces[color][Constants.ALL_PIECES] ^= fromToBB;
        pieceAtSquare[from] = movedPiece;
        pieceAtSquare[to] = Constants.NONE;

        // Capture
        if (Move.isCapture(move)) {

            // En passant
            if (Move.isEnPassant(move)) {
                int captureSquare = enPassantSquare + (color == WHITE ? -8 : 8);
                long captureSquareBB = Bitboard.SQUARE_BB_LOOK_UP[captureSquare];
                pieces[enemyColor][PAWN] ^= captureSquareBB;
                pieces[enemyColor][Constants.ALL_PIECES] ^= captureSquareBB;
                pieceAtSquare[captureSquare] = PAWN;
            }

            // Normal capture
            else {
                capturedPiece = capturedPieceHistory[moveCounter];
                pieces[enemyColor][capturedPiece] ^= toBB;
                pieces[enemyColor][Constants.ALL_PIECES] ^= toBB;
                pieceAtSquare[to] = capturedPiece;
            }
        }

        // Castling
        if (Move.isCastleKingSide(move)) {
            int rookFrom = (color == WHITE) ? Bitboard.H1 : Bitboard.H8;
            int rookTo   = (color == WHITE) ? Bitboard.F1 : Bitboard.F8;
            long rookMove = Bitboard.SQUARE_BB_LOOK_UP[rookFrom] | Bitboard.SQUARE_BB_LOOK_UP[rookTo];
            pieces[color][ROOK] ^= rookMove;
            pieces[color][ALL_PIECES] ^= rookMove;
            pieceAtSquare[rookFrom] = ROOK;
            pieceAtSquare[rookTo] = Constants.NONE;
        } else if (Move.isCastleQueenSide(move)) {
            int rookFrom = (color == WHITE) ? Bitboard.A1 : Bitboard.A8;
            int rookTo   = (color == WHITE) ? Bitboard.D1 : Bitboard.D8;
            long rookMove = Bitboard.SQUARE_BB_LOOK_UP[rookFrom] | Bitboard.SQUARE_BB_LOOK_UP[rookTo];
            pieces[color][ROOK] ^= rookMove;
            pieces[color][ALL_PIECES] ^= rookMove;
            pieceAtSquare[rookFrom] = ROOK;
            pieceAtSquare[rookTo] = Constants.NONE;
        }

        gameStatus = ONGOING;
    }

    public void parseFEN(String FEN) {
        BoardInitializer.initializeBoard(this, FEN);
    }

    public void clear() {
        pieces = new long[2][7];
        playerToMove = 0;
        castlingRights = 0;
        enPassantSquare = -1;
        halfmoveClock = 0;
        fullmoveNumber = 1;
        gameStatus = 0;
        pieceAtSquare = new int[64];
        moveCounter = 0;
        repetitionCount.clear();
        Arrays.fill(moveHistory, 0);
        Arrays.fill(castlingRightsHistory, 0);
        Arrays.fill(enPassantSquareHistory, 0);
        Arrays.fill(halfmoveClockHistory, 0);
        Arrays.fill(fullmoveNumberHistory, 0);
        Arrays.fill(hashKeyHistory, 0L);
    }

    public String generateFENString() {
        StringBuilder fen = new StringBuilder();

        // Piece placement
        for (int rank = 7; rank >= 0; rank--) {
            int emptyCount = 0;

            for (int file = 0; file < 8; file++) {
                int square = rank * 8 + file;
                boolean pieceFound = false;

                for (int color = 0; color <= 1; color++) {
                    for (int pieceType = 1; pieceType <= 6; pieceType++) {
                        if ((pieces[color][pieceType] & (1L << square)) != 0) {
                            if (emptyCount > 0) {
                                fen.append(emptyCount);
                                emptyCount = 0;
                            }
                            char pieceChar = Utils.pieceTypeToChar(pieceType, color);
                            fen.append(pieceChar);
                            pieceFound = true;
                            break;
                        }
                    }
                    if (pieceFound) break;
                }

                if (!pieceFound) {
                    emptyCount++;
                }
            }

            if (emptyCount > 0) {
                fen.append(emptyCount);
            }

            if (rank > 0) {
                fen.append('/');
            }
        }

        // Active color
        fen.append(' ');
        fen.append(playerToMove == 0 ? 'w' : 'b');

        // Castling rights
        fen.append(' ');
        StringBuilder castling = new StringBuilder();
        if ((castlingRights & 0b1000) != 0) castling.append('K');
        if ((castlingRights & 0b0100) != 0) castling.append('Q');
        if ((castlingRights & 0b0010) != 0) castling.append('k');
        if ((castlingRights & 0b0001) != 0) castling.append('q');
        fen.append(!castling.isEmpty() ? castling : "-");

        // En passant
        fen.append(' ');
        fen.append(enPassantSquare == -1 ? "-" : Utils.squareToAlgebraic(enPassantSquare));

        // Halfmove clock and fullmove number
        fen.append(' ');
        fen.append(halfmoveClock);
        fen.append(' ');
        fen.append(fullmoveNumber);

        return fen.toString();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");
        for (int i = 7; i >= 0; i--) {
            stringBuilder.append("+---+---+---+---+---+---+---+---+\n| ");
            for (int j = 0; j < 8; j++) {
                int square = i*8 + j;
                int pieceType = pieceAtSquare[square];
                int pieceColor = (Bitboard.SQUARE_BB_LOOK_UP[square] & pieces[WHITE][ALL_PIECES]) != 0 ? WHITE : BLACK;
                String pieceString = Utils.PIECE_NAMES[pieceType];
                pieceString = pieceColor == WHITE ? pieceString.toUpperCase() : pieceString.toLowerCase();
                stringBuilder.append(pieceString).append(" | ");
            }
            stringBuilder.append(i + 1).append("\n");
        }
        stringBuilder.append("+---+---+---+---+---+---+---+---+\n  a   b   c   d   e   f   g   h\n\n");
        stringBuilder.append(generateFENString());
        return stringBuilder.toString();
    }
}
