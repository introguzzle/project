package ru.chess.bot;

import ru.chess.label.Cell;
import ru.chess.model.Model;
import ru.chess.model.ValidMoves;

import java.util.Arrays;

public class AdvancedEvaluator extends AbstractEvaluator {

    // 0 - PAWN
    // 1 - ROOK
    // 2 - KNIGHT
    // 3 - BISHOP
    // 4 - QUEEN
    // 5 - CLOWN
    // 6 - WIZARD
    // 7 - TAMPLIER
    // 8 - KING

    private static final int[][] WHITE_PAWN_POSITION_WEIGHTS = new int[][] {
            {100, 100,  100,  100,  100,  100, 100, 100 },
            {50, 50,  50,  50,  50,  50,  50,  50 },
            {10, 10,  20,  30,  30,  20,  10,  10 },
            {5,   5,  10,  25,  25,  10,   5,   5,},
            {0,   0,   0,  20,  20,   0,   0,   0,},
            {5,  -5, -10,   0,   0, -10,  -5,   5,},
            {5,  10,  10, -20, -20,  10,  10,   5,},
            {0,   0,   0,   0,   0,   0,   0,   0 }
    };

    private static final int[][] BLACK_PAWN_POSITION_WEIGHTS
            = flip(WHITE_PAWN_POSITION_WEIGHTS);

    private static final int[][] WHITE_KNIGHT_POSITION_WEIGHTS = new int[][] {
            {-50, -40, -30, -30, -30, -30, -40, -50},
            {-40, -20,   0,   0,   0,   0, -20, -40},
            {-30,   0,  10,  15,  15,  10,   0, -30},
            {-30,   5,  15,  20,  20,  15,   5, -30},
            {-30,   0,  15,  20,  20,  15,   0, -30},
            {-30,   5,  10,  15,  15,  10,   5, -30},
            {-40, -20,   0,   5,   5,   0, -20, -40},
            {-50, -40, -30, -30, -30, -30, -40, -50},
    };

    private static final int[][] BLACK_KNIGHT_POSITION_WEIGHTS
            = flip(WHITE_KNIGHT_POSITION_WEIGHTS);

    private static final int[][] WHITE_BISHOP_POSITION_WEIGHTS = new int[][] {
            {-20,-10,-10,-10,-10,-10,-10,-20},
            {-10,  0,  0,  0,  0,  0,  0,-10},
            {-10,  0,  5, 10, 10,  5,  0,-10},
            {-10,  5,  5, 10, 10,  5,  5,-10},
            {-10,  0, 10, 10, 10, 10,  0,-10},
            {-10, 10, 10, 10, 10, 10, 10,-10},
            {-10,  5,  0,  0,  0,  0,  5,-10},
            {-20,-10,-10,-10,-10,-10,-10,-20},
    };

    private static final int[][] BLACK_BISHOP_POSITION_WEIGHTS
            = flip(WHITE_BISHOP_POSITION_WEIGHTS);

    private static final int[][] WHITE_ROOK_POSITION_WEIGHTS = new int[][] {
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 5, 10, 10, 10, 10, 10, 10,  5},
            {-5,  0,  0,  0,  0,  0,  0, -5},
            {-5,  0,  0,  0,  0,  0,  0, -5},
            {-5,  0,  0,  0,  0,  0,  0, -5},
            {-5,  0,  0,  0,  0,  0,  0, -5},
            {-5,  0,  0,  0,  0,  0,  0, -5},
            {  0,  0,  0,  5,  5,  0,  0,  0},
    };

    private static final int[][] BLACK_ROOK_POSITION_WEIGHTS
            = flip(WHITE_ROOK_POSITION_WEIGHTS);

    private static final int[][] WHITE_QUEEN_POSITION_WEIGHTS = new int[][] {
            {-20,-10,-10, -5, -5,-10,-10,-20},
            {-10,  0,  0,  0,  0,  0,  0,-10},
            {-10,  0,  5,  5,  5,  5,  0,-10},
            {-5,  0,  5,  5,  5,  5,  0, -5},
            {0,  0,  5,  5,  5,  5,  0, -5},
            {-10,  5,  5,  5,  5,  5,  0,-10},
            {-10,  0,  5,  0,  0,  0,  0,-10},
            {-20,-10,-10, -5, -5,-10,-10,-20},
    };

    private static final int[][] BLACK_QUEEN_POSITION_WEIGHTS
            = flip(WHITE_QUEEN_POSITION_WEIGHTS);

    private static final int[][] WHITE_KING_MID_GAME_POSITION_WEIGHTS = new int[][] {
            {-30,-40,-40,-50,-50,-40,-40,-30},
            {-30,-40,-40,-50,-50,-40,-40,-30},
            {-30,-40,-40,-50,-50,-40,-40,-30},
            {-30,-40,-40,-50,-50,-40,-40,-30},
            {-20,-30,-30,-40,-40,-30,-30,-20},
            {-10,-20,-20,-20,-20,-20,-20,-10},
            {20, 20,  0,  0,  0,  0, 20, 20},
            {20, 30, 10,  0,  0, 10, 30, 20},

    };

    private static final int[][] BLACK_KING_MID_GAME_POSITION_WEIGHTS
            = flip(WHITE_KING_MID_GAME_POSITION_WEIGHTS);

    private static final int[][] WHITE_KING_END_GAME_POSITION_WEIGHTS = new int[][] {
            {-50,-40,-30,-20,-20,-30,-40,-50},
            {-30,-20,-10,  0,  0,-10,-20,-30},
            {-30,-10, 20, 30, 30, 20,-10,-30},
            {-30,-10, 30, 40, 40, 30,-10,-30},
            {-30,-10, 30, 40, 40, 30,-10,-30},
            {-30,-10, 20, 30, 30, 20,-10,-30},
            {-30,-30,  0,  0,  0,  0,-30,-30},
            {-50,-30,-30,-30,-30,-30,-30,-50},
    };

    private static final int[][] BLACK_KING_END_GAME_POSITION_WEIGHTS
            = flip(WHITE_KING_END_GAME_POSITION_WEIGHTS);

    public AdvancedEvaluator(Model model) {
        super(model);
    }

    @Override
    public int evaluate() {
        int boardValue = 0;
        int mobility   = 0;

        int whiteBlockedPawns = 0;
        int blackBlockedPawns = 0;

        for (int i = 0; i < Model.VERTICAL_BOUND; i++)
            for (int j = 0; j < Model.HORIZONTAL_BOUND; j++) {
                Cell cell = model.getBoard().cells[i][j];

                boardValue -= (cell.pieceType.value + evaluateWeight(cell, i, j));

                int size = ValidMoves.get(model, cell).size();
                mobility   += size;

                if (cell.absolutePieceType.isWhite() && size == 0)
                    whiteBlockedPawns++;

                if (cell.absolutePieceType.isBlack() && size == 0)
                    blackBlockedPawns++;
            }

        return boardValue + 10 * mobility + 50 * (blackBlockedPawns - whiteBlockedPawns);
    }

    private static int evaluateWeight(Cell cell, int i, int j) {
        return switch(cell.pieceType) {
            case NONE -> 0;

            case WHITE_PAWN     -> WHITE_PAWN_POSITION_WEIGHTS[i][j];
            case WHITE_ROOK     -> WHITE_ROOK_POSITION_WEIGHTS[i][j];
            case WHITE_KNIGHT   -> WHITE_KNIGHT_POSITION_WEIGHTS[i][j];
            case WHITE_BISHOP   -> WHITE_BISHOP_POSITION_WEIGHTS[i][j];
            case WHITE_QUEEN    -> WHITE_QUEEN_POSITION_WEIGHTS[i][j];
            case WHITE_CLOWN    -> 0;
            case WHITE_WIZARD   -> 0;
            case WHITE_TAMPLIER -> 0;
            case WHITE_KING     -> WHITE_KING_END_GAME_POSITION_WEIGHTS[i][j];

            case BLACK_PAWN     -> BLACK_PAWN_POSITION_WEIGHTS[i][j];
            case BLACK_ROOK     -> BLACK_ROOK_POSITION_WEIGHTS[i][j];
            case BLACK_KNIGHT   -> BLACK_KNIGHT_POSITION_WEIGHTS[i][j];
            case BLACK_BISHOP   -> BLACK_BISHOP_POSITION_WEIGHTS[i][j];
            case BLACK_QUEEN    -> BLACK_QUEEN_POSITION_WEIGHTS[i][j];
            case BLACK_CLOWN    -> 0;
            case BLACK_WIZARD   -> 0;
            case BLACK_TAMPLIER -> 0;
            case BLACK_KING     -> BLACK_KING_END_GAME_POSITION_WEIGHTS[i][j];
        };
    }

    public static int[][] flip(int[][] source) {
        int[][] dst = new int[source.length][source[0].length];

        for (int i = 0; i < source.length; i++)
            System.arraycopy(source[i], 0, dst[source.length - 1 - i], 0, source[0].length);

        return dst;

    }
}
