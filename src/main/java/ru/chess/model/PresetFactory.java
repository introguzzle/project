package ru.chess.model;

import ru.chess.PieceType;
import ru.chess.position.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class PresetFactory {

    private PresetFactory() {

    }

    static int VERTICAL_BOUND   = Position.VERTICAL_BOUND;
    static int HORIZONTAL_BOUND = Position.HORIZONTAL_BOUND;

    public static String create() {
        return create(true, true);
    }

    public static String create(boolean turn) {
        return create(turn, true);
    }

    public static String create(boolean turn, boolean castlingPossible) {
         return "O" + (turn ? "W" : "B") + (castlingPossible ? "FFFFFF" : "TTTTTT") + " /"
                 + setPawns() + setOthers() + setKingsAndRooks();
    }

    static int random(int left, int right) {
        return ThreadLocalRandom.current().nextInt(left, right);
    }

    static int randomKingHorizontalStart() {
        return random(1, HORIZONTAL_BOUND - 1);
    }

    static String setPawns() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < HORIZONTAL_BOUND; i++)
            sb.
                    append(" ").
                    append(PieceType.BLACK_PAWN.code).
                    append(new Position(1, i).getChessPosition()).

                    append(" ").
                    append(PieceType.WHITE_PAWN.code).
                    append(new Position(VERTICAL_BOUND - 2, i).getChessPosition());

        return sb.toString();
    }

    static String setKingsAndRooks() {
        StringBuilder sb = new StringBuilder();

        Position whiteKingPosition = new Position(VERTICAL_BOUND - 1, randomKingHorizontalStart());
        Position blackKingPosition = new Position(0, whiteKingPosition.getWidth());

        sb.append(" ").append(PieceType.WHITE_KING.code).append(whiteKingPosition.getChessPosition());
        sb.append(" ").append(PieceType.BLACK_KING.code).append(blackKingPosition.getChessPosition());

        Position whiteLeftRookPosition  = new Position(VERTICAL_BOUND - 1, random(0, whiteKingPosition.getWidth()));
        Position whiteRightRookPosition = new Position(VERTICAL_BOUND - 1, random(whiteKingPosition.getWidth() + 1, HORIZONTAL_BOUND));

        sb.append(" ").append(PieceType.WHITE_ROOK.code).append(whiteLeftRookPosition.getChessPosition());
        sb.append(" ").append(PieceType.WHITE_ROOK.code).append(whiteRightRookPosition.getChessPosition());

        Position blackLeftRookPosition  = new Position(0, whiteLeftRookPosition.getWidth());
        Position blackRightRookPosition = new Position(0, whiteRightRookPosition.getWidth());

        sb.append(" ").append(PieceType.BLACK_ROOK.code).append(blackLeftRookPosition.getChessPosition());
        sb.append(" ").append(PieceType.BLACK_ROOK.code).append(blackRightRookPosition.getChessPosition());

        return sb.toString();
    }

    static String setOthers() {
        List<String> list = new ArrayList<>();

        var whites = PieceType.allWhites();
        var blacks = PieceType.allBlacks();

        for (int i = 0; i < HORIZONTAL_BOUND; i++) {
            int pick = random(0, whites.size() - 1);

            list.add(" " + whites.get(pick).code + new Position(VERTICAL_BOUND - 1, i).getChessPosition());
            list.add(" " + blacks.get(pick).code + new Position(0, i).getChessPosition());
        }

        StringBuilder sb = new StringBuilder();

        for (String s: list) {
            sb.append(s);
        }

        return sb.toString();
    }
}
