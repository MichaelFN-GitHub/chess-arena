package com.MichaelFN.chess.v5.move;

import com.MichaelFN.chess.v5.board.Board;

import static com.MichaelFN.chess.v5.Utils.*;
import static com.MichaelFN.chess.v5.board.Bitboard.*;
import static com.MichaelFN.chess.v5.Constants.*;
import static com.MichaelFN.chess.v5.move.MoveTables.*;

public class MoveGenerator {
    public static int[] moves;
    public static int moveCounter;

    public static void generatePseudoLegalMoves(Board board) {
        moves = new int[MAX_MOVES_IN_POSITION];
        moveCounter = 0;

        int player = board.playerToMove;
        int opponent = 1 - board.playerToMove;
        long allies = board.pieces[player][ALL_PIECES];
        long enemies = board.pieces[opponent][ALL_PIECES];
        long occupancy = allies | enemies;
        long empty = ~occupancy;

        long[] pieces = board.pieces[player];

        generatePawnMoves(pieces[PAWN], player, empty, enemies, board.enPassantSquare);
        generateKnightMoves(pieces[KNIGHT], allies, enemies);
        generateBishopMoves(pieces[BISHOP], allies, enemies, occupancy);
        generateRookMoves(pieces[ROOK], allies, enemies, occupancy);
        generateQueenMoves(pieces[QUEEN], allies, enemies, occupancy);
        generateKingMoves(pieces[KING], allies, enemies);
        generateCastlingMoves(occupancy, board.castlingRights, player, board);
    }

    public static void generatePseudoLegalCaptures(Board board) {
        moves = new int[MAX_MOVES_IN_POSITION];
        moveCounter = 0;

        int player = board.playerToMove;
        int opponent = 1 - board.playerToMove;
        long allies = board.pieces[player][ALL_PIECES];
        long enemies = board.pieces[opponent][ALL_PIECES];
        long occupancy = allies | enemies;

        long[] pieces = board.pieces[player];

        generatePawnCaptures(pieces[PAWN], player, enemies, board.enPassantSquare);
        generateKnightCaptures(pieces[KNIGHT], enemies);
        generateBishopCaptures(pieces[BISHOP], enemies, occupancy);
        generateRookCaptures(pieces[ROOK], enemies, occupancy);
        generateQueenCaptures(pieces[QUEEN], enemies, occupancy);
        generateKingCaptures(pieces[KING], enemies);
    }

    private static void generateCastlingMoves(long occupancy, int castlingRights, int color, Board board) {
        // White Castling
        if (color == WHITE) {
            // Kingside
            if ((castlingRights & CASTLE_WHITE_KINGSIDE) != 0) {
                boolean empty = (occupancy & (SQUARE_BB_LOOK_UP[F1] | SQUARE_BB_LOOK_UP[G1])) == 0;
                boolean safe = !(
                        isSquareAttacked(board, E1, BLACK, occupancy) ||
                                isSquareAttacked(board, F1, BLACK, occupancy) ||
                                isSquareAttacked(board, G1, BLACK, occupancy)
                );
                if (empty && safe) {
                    addMove(Move.createCastleKingSide(E1, G1));
                }
            }

            // Queenside
            if ((castlingRights & CASTLE_WHITE_QUEENSIDE) != 0) {
                boolean empty = (occupancy & (SQUARE_BB_LOOK_UP[B1] | SQUARE_BB_LOOK_UP[C1] | SQUARE_BB_LOOK_UP[D1])) == 0;
                boolean safe = !(
                        isSquareAttacked(board, E1, BLACK, occupancy) ||
                                isSquareAttacked(board, D1, BLACK, occupancy) ||
                                isSquareAttacked(board, C1, BLACK, occupancy)
                );
                if (empty && safe) {
                    addMove(Move.createCastleQueenSide(E1, C1));
                }
            }
        }

        // Black Castling
        else {
            // Kingside
            if ((castlingRights & CASTLE_BLACK_KINGSIDE) != 0) {
                boolean empty = (occupancy & (SQUARE_BB_LOOK_UP[F8] | SQUARE_BB_LOOK_UP[G8])) == 0;
                boolean safe = !(
                        isSquareAttacked(board, E8, WHITE, occupancy) ||
                                isSquareAttacked(board, F8, WHITE, occupancy) ||
                                isSquareAttacked(board, G8, WHITE, occupancy)
                );
                if (empty && safe) {
                    addMove(Move.createCastleKingSide(E8, G8));
                }
            }

            // Queenside
            if ((castlingRights & CASTLE_BLACK_QUEENSIDE) != 0) {
                boolean empty = (occupancy & (SQUARE_BB_LOOK_UP[B8] | SQUARE_BB_LOOK_UP[C8] | SQUARE_BB_LOOK_UP[D8])) == 0;
                boolean safe = !(
                        isSquareAttacked(board, E8, WHITE, occupancy) ||
                                isSquareAttacked(board, D8, WHITE, occupancy) ||
                                isSquareAttacked(board, C8, WHITE, occupancy)
                );
                if (empty && safe) {
                    addMove(Move.createCastleQueenSide(E8, C8));
                }
            }
        }
    }

    private static void generateKingMoves(long king, long allies, long enemies) {
        int fromSquare = lsb(king);
        long moves = KING_MOVE_MASKS[fromSquare] & ~allies;

        while (moves != 0) {
            int toSquare = lsb(moves);
            boolean isCapture = hasBit(enemies, toSquare);
            int move = isCapture
                    ? Move.createCapture(fromSquare, toSquare)
                    : Move.createQuietMove(fromSquare, toSquare);
            addMove(move);
            moves = clearLsb(moves);
        }
    }

    private static void generateKingCaptures(long king, long enemies) {
        int fromSquare = lsb(king);
        long moves = KING_MOVE_MASKS[fromSquare] & enemies;
        while (moves != 0) {
            int toSquare = lsb(moves);
            int move = Move.createCapture(fromSquare, toSquare);
            addMove(move);
            moves = clearLsb(moves);
        }
    }

    private static void generateQueenMoves(long queens, long allies, long enemies, long occupancy) {
        generateBishopMoves(queens, allies, enemies, occupancy);
        generateRookMoves(queens, allies, enemies, occupancy);
    }

    private static void generateQueenCaptures(long queens, long enemies, long occupancy) {
        generateBishopCaptures(queens, enemies, occupancy);
        generateRookCaptures(queens, enemies, occupancy);
    }

    private static void generateBishopMoves(long bishops, long allies, long enemies, long occupancy) {
        while (bishops != 0) {
            int fromSquare = lsb(bishops);
            long moves = getBishopMoves(fromSquare, occupancy) & ~allies;

            while (moves != 0) {
                int toSquare = lsb(moves);
                boolean isCapture = hasBit(enemies, toSquare);
                int move = isCapture
                        ? Move.createCapture(fromSquare, toSquare)
                        : Move.createQuietMove(fromSquare, toSquare);
                addMove(move);
                moves = clearLsb(moves);
            }

            bishops = clearLsb(bishops);
        }
    }

    private static void generateBishopCaptures(long bishops, long enemies, long occupancy) {
        while (bishops != 0) {
            int fromSquare = lsb(bishops);
            long moves = getBishopMoves(fromSquare, occupancy) & enemies;

            while (moves != 0) {
                int toSquare = lsb(moves);
                int move = Move.createCapture(fromSquare, toSquare);
                addMove(move);
                moves = clearLsb(moves);
            }

            bishops = clearLsb(bishops);
        }
    }

    private static void generateRookMoves(long rooks, long allies, long enemies, long occupancy) {
        while (rooks != 0) {
            int fromSquare = lsb(rooks);
            long moves = getRookMoves(fromSquare, occupancy) & ~allies;

            while (moves != 0) {
                int toSquare = lsb(moves);
                boolean isCapture = hasBit(enemies, toSquare);
                int move = isCapture
                        ? Move.createCapture(fromSquare, toSquare)
                        : Move.createQuietMove(fromSquare, toSquare);
                addMove(move);
                moves = clearLsb(moves);
            }

            rooks = clearLsb(rooks);
        }
    }

    private static void generateRookCaptures(long rooks, long enemies, long occupancy) {
        while (rooks != 0) {
            int fromSquare = lsb(rooks);
            long moves = getRookMoves(fromSquare, occupancy) & enemies;

            while (moves != 0) {
                int toSquare = lsb(moves);
                int move = Move.createCapture(fromSquare, toSquare);
                addMove(move);
                moves = clearLsb(moves);
            }

            rooks = clearLsb(rooks);
        }
    }

    private static void generateKnightMoves(long knights, long allies, long enemies) {
        while (knights != 0) {
            int fromSquare = lsb(knights);
            long targets = ~(allies);
            long moves = KNIGHT_MOVE_MASKS[fromSquare] & targets;

            while (moves != 0) {
                int toSquare = lsb(moves);
                boolean isCapture = hasBit(enemies, toSquare);
                int move = isCapture
                        ? Move.createCapture(fromSquare, toSquare)
                        : Move.createQuietMove(fromSquare, toSquare);
                addMove(move);
                moves = clearLsb(moves);
            }

            knights = clearLsb(knights);
        }
    }

    private static void generateKnightCaptures(long knights, long enemies) {
        while (knights != 0) {
            int fromSquare = lsb(knights);
            long moves = KNIGHT_MOVE_MASKS[fromSquare] & enemies;

            while (moves != 0) {
                int toSquare = lsb(moves);
                int move = Move.createCapture(fromSquare, toSquare);
                addMove(move);
                moves = clearLsb(moves);
            }
            knights = clearLsb(knights);
        }
    }

    private static void generatePawnMoves(long pawns, int color, long empty, long enemies, int enPassantSquare) {
        long enPassantBB = enPassantSquare == -1 ? 0 : SQUARE_BB_LOOK_UP[enPassantSquare];

        if (color == WHITE) {
            long singlePushes = (pawns << 8) & empty;
            long doublePushes = ((singlePushes & RANK_3) << 8) & empty;
            long leftCaptures = ((pawns << 7) & enemies) & ~FILE_H;
            long rightCaptures = ((pawns << 9) & enemies) & ~FILE_A;
            long enPassantLeftCaptures = ((pawns << 7) & enPassantBB) & ~FILE_H;
            long enPassantRightCaptures = ((pawns << 9) & enPassantBB) & ~FILE_A;

            // Loop through each move and create it
            while (singlePushes != 0) {
                int toSquare = lsb(singlePushes);
                int fromSquare = toSquare - 8;
                if (toSquare > 55) {
                    addMove(Move.createPromotionMove(fromSquare, toSquare, QUEEN));
                    addMove(Move.createPromotionMove(fromSquare, toSquare, ROOK));
                    addMove(Move.createPromotionMove(fromSquare, toSquare, BISHOP));
                    addMove(Move.createPromotionMove(fromSquare, toSquare, KNIGHT));
                } else {
                    addMove(Move.createQuietMove(fromSquare, toSquare));
                }
                singlePushes = clearLsb(singlePushes);
            }

            while (doublePushes != 0) {
                int toSquare = lsb(doublePushes);
                int fromSquare = toSquare - 16;
                int move = Move.createDoublePawnPush(fromSquare, toSquare);
                addMove(move);
                doublePushes = clearLsb(doublePushes);
            }

            while (leftCaptures != 0) {
                int toSquare = lsb(leftCaptures);
                int fromSquare = toSquare - 7;
                if (toSquare > 55) {
                    addMove(Move.createPromotionCapture(fromSquare, toSquare, QUEEN));
                    addMove(Move.createPromotionCapture(fromSquare, toSquare, ROOK));
                    addMove(Move.createPromotionCapture(fromSquare, toSquare, BISHOP));
                    addMove(Move.createPromotionCapture(fromSquare, toSquare, KNIGHT));
                } else {
                    addMove(Move.createCapture(fromSquare, toSquare));
                }
                leftCaptures = clearLsb(leftCaptures);
            }

            while (rightCaptures != 0) {
                int toSquare = lsb(rightCaptures);
                int fromSquare = toSquare - 9;
                if (toSquare > 55) {
                    addMove(Move.createPromotionCapture(fromSquare, toSquare, QUEEN));
                    addMove(Move.createPromotionCapture(fromSquare, toSquare, ROOK));
                    addMove(Move.createPromotionCapture(fromSquare, toSquare, BISHOP));
                    addMove(Move.createPromotionCapture(fromSquare, toSquare, KNIGHT));
                } else {
                    addMove(Move.createCapture(fromSquare, toSquare));
                }
                rightCaptures = clearLsb(rightCaptures);
            }

            while (enPassantLeftCaptures != 0) {
                int toSquare = lsb(enPassantLeftCaptures);
                int fromSquare = toSquare - 7;
                int move = Move.createEnPassantCapture(fromSquare, toSquare);
                addMove(move);
                enPassantLeftCaptures = clearLsb(enPassantLeftCaptures);
            }

            while (enPassantRightCaptures != 0) {
                int toSquare = lsb(enPassantRightCaptures);
                int fromSquare = toSquare - 9;
                int move = Move.createEnPassantCapture(fromSquare, toSquare);
                addMove(move);
                enPassantRightCaptures = clearLsb(enPassantRightCaptures);
            }
        }

        // Black
        else {
            long singlePushes = (pawns >>> 8) & empty;
            long doublePushes = ((singlePushes & RANK_6) >>> 8) & empty;
            long leftCaptures  = ((pawns >>> 9) & enemies) & ~FILE_H;
            long rightCaptures = ((pawns >>> 7) & enemies) & ~FILE_A;
            long enPassantLeftCaptures = ((pawns >>> 9) & enPassantBB) & ~FILE_H;
            long enPassantRightCaptures = ((pawns >>> 7) & enPassantBB) & ~FILE_A;

            // Loop through each move and create it
            while (singlePushes != 0) {
                int toSquare = lsb(singlePushes);
                int fromSquare = toSquare + 8;
                if (toSquare < 8) {
                    addMove(Move.createPromotionMove(fromSquare, toSquare, QUEEN));
                    addMove(Move.createPromotionMove(fromSquare, toSquare, ROOK));
                    addMove(Move.createPromotionMove(fromSquare, toSquare, BISHOP));
                    addMove(Move.createPromotionMove(fromSquare, toSquare, KNIGHT));
                } else {
                    addMove(Move.createQuietMove(fromSquare, toSquare));
                }
                singlePushes = clearLsb(singlePushes);
            }

            while (doublePushes != 0) {
                int toSquare = lsb(doublePushes);
                int fromSquare = toSquare + 16;
                int move = Move.createDoublePawnPush(fromSquare, toSquare);
                addMove(move);
                doublePushes = clearLsb(doublePushes);
            }

            while (leftCaptures != 0) {
                int toSquare = lsb(leftCaptures);
                int fromSquare = toSquare + 7;
                if (toSquare < 8) {
                    addMove(Move.createPromotionCapture(fromSquare, toSquare, QUEEN));
                    addMove(Move.createPromotionCapture(fromSquare, toSquare, ROOK));
                    addMove(Move.createPromotionCapture(fromSquare, toSquare, BISHOP));
                    addMove(Move.createPromotionCapture(fromSquare, toSquare, KNIGHT));
                } else {
                    addMove(Move.createCapture(fromSquare, toSquare));
                }
                leftCaptures = clearLsb(leftCaptures);
            }

            while (rightCaptures != 0) {
                int toSquare = lsb(rightCaptures);
                int fromSquare = toSquare + 9;
                if (toSquare < 8) {
                    addMove(Move.createPromotionCapture(fromSquare, toSquare, QUEEN));
                    addMove(Move.createPromotionCapture(fromSquare, toSquare, ROOK));
                    addMove(Move.createPromotionCapture(fromSquare, toSquare, BISHOP));
                    addMove(Move.createPromotionCapture(fromSquare, toSquare, KNIGHT));
                } else {
                    addMove(Move.createCapture(fromSquare, toSquare));
                }
                rightCaptures = clearLsb(rightCaptures);
            }

            while (enPassantLeftCaptures != 0) {
                int toSquare = lsb(enPassantLeftCaptures);
                int fromSquare = toSquare + 7;
                int move = Move.createEnPassantCapture(fromSquare, toSquare);
                addMove(move);
                enPassantLeftCaptures = clearLsb(enPassantLeftCaptures);
            }

            while (enPassantRightCaptures != 0) {
                int toSquare = lsb(enPassantRightCaptures);
                int fromSquare = toSquare + 9;
                int move = Move.createEnPassantCapture(fromSquare, toSquare);
                addMove(move);
                enPassantRightCaptures = clearLsb(enPassantRightCaptures);
            }
        }
    }

    private static void generatePawnCaptures(long pawns, int color, long enemies, int enPassantSquare) {
        long enPassantBB = SQUARE_BB_LOOK_UP[enPassantSquare];

        if (color == WHITE) {
            long leftCaptures = ((pawns << 7) & enemies) & ~FILE_H;
            long rightCaptures = ((pawns << 9) & enemies) & ~FILE_A;
            long enPassantLeftCaptures = ((pawns << 7) & enPassantBB) & ~FILE_H;
            long enPassantRightCaptures = ((pawns << 9) & enPassantBB) & ~FILE_A;

            while (leftCaptures != 0) {
                int toSquare = lsb(leftCaptures);
                int fromSquare = toSquare - 7;
                int move = Move.createCapture(fromSquare, toSquare);
                addMove(move);
                leftCaptures = clearLsb(leftCaptures);
            }

            while (rightCaptures != 0) {
                int toSquare = lsb(rightCaptures);
                int fromSquare = toSquare - 9;
                int move = Move.createCapture(fromSquare, toSquare);
                addMove(move);
                rightCaptures = clearLsb(rightCaptures);
            }

            while (enPassantLeftCaptures != 0) {
                int toSquare = lsb(enPassantLeftCaptures);
                int fromSquare = toSquare - 7;
                int move = Move.createEnPassantCapture(fromSquare, toSquare);
                addMove(move);
                enPassantLeftCaptures = clearLsb(enPassantLeftCaptures);
            }

            while (enPassantRightCaptures != 0) {
                int toSquare = lsb(enPassantRightCaptures);
                int fromSquare = toSquare - 9;
                int move = Move.createEnPassantCapture(fromSquare, toSquare);
                addMove(move);
                enPassantRightCaptures = clearLsb(enPassantRightCaptures);
            }
        }

        // Black
        else {
            long leftCaptures  = (pawns >>> 9) & enemies & ~FILE_H;
            long rightCaptures = (pawns >>> 7) & enemies & ~FILE_A;
            long enPassantLeftCaptures = ((pawns >>> 9) & enPassantBB) & ~FILE_H;
            long enPassantRightCaptures = ((pawns >>> 7) & enPassantBB) & ~FILE_A;

            while (leftCaptures != 0) {
                int toSquare = lsb(leftCaptures);
                int fromSquare = toSquare + 7;
                int move = Move.createCapture(fromSquare, toSquare);
                addMove(move);
                leftCaptures = clearLsb(leftCaptures);
            }

            while (rightCaptures != 0) {
                int toSquare = lsb(rightCaptures);
                int fromSquare = toSquare + 9;
                int move = Move.createCapture(fromSquare, toSquare);
                addMove(move);
                rightCaptures = clearLsb(rightCaptures);
            }

            while (enPassantLeftCaptures != 0) {
                int toSquare = lsb(enPassantLeftCaptures);
                int fromSquare = toSquare + 7;
                int move = Move.createEnPassantCapture(fromSquare, toSquare);
                addMove(move);
                enPassantLeftCaptures = clearLsb(enPassantLeftCaptures);
            }

            while (enPassantRightCaptures != 0) {
                int toSquare = lsb(enPassantRightCaptures);
                int fromSquare = toSquare + 9;
                int move = Move.createEnPassantCapture(fromSquare, toSquare);
                addMove(move);
                enPassantRightCaptures = clearLsb(enPassantRightCaptures);
            }
        }
    }

    private static void addMove(int move) {
        moves[moveCounter++] = move;
    }
}
