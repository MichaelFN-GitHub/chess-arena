package com.MichaelFN.chess.v1;

public class BoardInitializer {
    public static final String[][] POSITION_STRING = {
            {"r", "n", "b", "q", "k", "b", "n", "r"},
            {"p", "p", "p", "p", "p", "p", "p", "p"},
            {"",  "",  "",  "",  "",  "",  "",  ""},
            {"",  "",  "",  "",  "",  "",  "",  ""},
            {"",  "",  "",  "",  "",  "",  "",  ""},
            {"",  "",  "",  "",  "",  "",  "",  ""},
            {"P", "P", "P", "P", "P", "P", "P", "P"},
            {"R", "N", "B", "Q", "K", "B", "N", "R"}
    };
    public static final String STARTING_POSITION_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    private final BoardState boardState;

    public BoardInitializer(BoardState boardState) {
        this.boardState = boardState;
    }

    public void initializeStartingPosition() {
        initializeFromFen(STARTING_POSITION_FEN);
    }

    public void initializeFromFen(String FEN) {
        String[] fenSections = FEN.split(" ");
        if (fenSections.length != 6) {
            throw new IllegalArgumentException("Invalid number of sections in FEN string: " + FEN);
        }

        String[] fenRanks = fenSections[0].split("/");
        if (fenRanks.length != 8) {
            throw new IllegalArgumentException("Invalid number of board rows in FEN string: " + FEN);
        }

        Piece[][] position = new Piece[8][8];
        int[][] kingPositions = new int[2][2];
        int remainingPieces = 0;
        for (int row = 0; row < 8; row++) {
            int col = 0;
            for (int j = 0; j < fenRanks[row].length(); j++) {
                char c = fenRanks[row].charAt(j);
                if (Character.isDigit(c)) {
                    col += Character.getNumericValue(c);
                } else {
                    String pieceString = String.valueOf(c);
                    Piece piece = Utils.stringToPiece(pieceString);
                    if (piece.type() == PieceType.KING) {
                        kingPositions[piece.color().ordinal()] = new int[]{row,col};
                    }
                    position[row][col] = piece;
                    remainingPieces++;
                    col++;
                }
            }
        }
        boardState.setPosition(position);
        boardState.setKingPositions(kingPositions);
        boardState.setRemainingPieces(remainingPieces);
        boardState.setPlayerToMove(fenSections[1].equals("w") ? Color.WHITE : Color.BLACK);

        boolean[][] castlingRights = new boolean[2][2];
        castlingRights[0][0] = fenSections[2].contains("K");
        castlingRights[0][1] = fenSections[2].contains("Q");
        castlingRights[1][0] = fenSections[2].contains("k");
        castlingRights[1][1] = fenSections[2].contains("q");
        boardState.setCastlingRights(castlingRights);
        boardState.setEnPassantSquare(Utils.squareStringToCoords(fenSections[3]));
        boardState.setHalfmoveClock(Integer.parseInt(fenSections[4]));
        boardState.setFullmoveNumber(Integer.parseInt(fenSections[5]));
    }

    public void initializeFromStringArray(String[][] stringArray) {
        Piece[][] position = new Piece[8][8];
        int[][] kingPositions = new int[2][2];
        int remainingPieces = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                String pieceString = stringArray[i][j];
                Piece piece = Utils.stringToPiece(pieceString);
                position[i][j] = piece;

                if (piece.type() == PieceType.KING) {
                    kingPositions[piece.color().ordinal()] = new int[]{i,j};
                }

                if (piece != null) remainingPieces++;
            }
        }
        boardState.setPosition(position);
        boardState.setKingPositions(kingPositions);
        boardState.setRemainingPieces(remainingPieces);
        boardState.setPlayerToMove(Color.WHITE);
        boardState.setCastlingRights(new boolean[][]{{true,true}, {true,true}});
        boardState.setEnPassantSquare(null);
        boardState.setHalfmoveClock(0);
        boardState.setFullmoveNumber(0);
    }
}
