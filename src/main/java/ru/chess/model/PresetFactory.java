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

    public static String createDefaultPieceSetup() {
        return trim(Presets.DEFAULT.substring(11));
    }

    public static String createDefaultRandomPieceSetup() {
        return trim(setPawns() + createOthers() + setKingsAndRooks());
    }

    public static String createExtendedPieceSetup() {
        return trim(create(true, true).substring(12));
    }

    public static String create() {
        return create(true, true);
    }

    public static String create(boolean turn) {
        return create(turn, true);
    }

    public static String create(boolean turn, boolean castlingPossible) {
         return "O" + (turn ? "W" : "B") + (castlingPossible ? "FFFFFF" : "TTTTTT")
                 // This indicates this is start and not reinitialized
                 + "F"
                 + " /"
                 + setPawns() + createExtendedOthers() + setKingsAndRooks();
    }

    private static String trim(String pieceSetup) {
        return pieceSetup.charAt(0) == ' ' ? pieceSetup.substring(1) : pieceSetup;
    }

    private static int random(int left, int right) {
        return ThreadLocalRandom.current().nextInt(left, right);
    }

    private static int randomKingHorizontalStart() {
        return random(1, HORIZONTAL_BOUND - 1);
    }

    private static String setPawns() {
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

    private static String setKingsAndRooks() {
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

    private static String createOthers() {
        List<String> list = new ArrayList<>();

        var whites = PieceType.allWhites();
        var blacks = PieceType.allBlacks();

        for (int i = 0; i < HORIZONTAL_BOUND; i++) {
            int pick = random(1, 5);

            list.add(" " + whites.get(pick).code + new Position(VERTICAL_BOUND - 1, i).getChessPosition());
            list.add(" " + blacks.get(pick).code + new Position(0, i).getChessPosition());
        }

        StringBuilder sb = new StringBuilder();

        for (String s: list) {
            sb.append(s);
        }

        return sb.toString();
    }

    private static String createExtendedOthers() {
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
