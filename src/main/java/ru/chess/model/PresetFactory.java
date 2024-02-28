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
        StringBuilder built = new StringBuilder();

        for (int i = 0; i < HORIZONTAL_BOUND; i++) {
            built.
                    append(" ").append("bp").append(new Position(1, i).getChessPosition()).
                    append(" ").append("wp").append(new Position(VERTICAL_BOUND - 2, i).getChessPosition());
        }

        return built.toString();
    }

    static String setKingsAndRooks() {
        Position whiteKingPosition = new Position(VERTICAL_BOUND - 1, randomKingHorizontalStart());
        Position blackKingPosition = new Position(0, whiteKingPosition.getWidth());

        String wK = " wK" + whiteKingPosition.getChessPosition();
        String bK = " bK" + blackKingPosition.getChessPosition();

        Position whiteLeftRookPosition  = new Position(VERTICAL_BOUND - 1, random(0, whiteKingPosition.getWidth()));
        Position whiteRightRookPosition = new Position(VERTICAL_BOUND - 1, random(whiteKingPosition.getWidth() + 1, HORIZONTAL_BOUND));

        String wLR = " wr" + whiteLeftRookPosition.getChessPosition();
        String wRR = " wr" + whiteRightRookPosition.getChessPosition();

        Position blackLeftRookPosition  = new Position(0, whiteLeftRookPosition.getWidth());
        Position blackRightRookPosition = new Position(0, whiteRightRookPosition.getWidth());

        String bLR = " br" + blackLeftRookPosition.getChessPosition();
        String bRR = " br" + blackRightRookPosition.getChessPosition();

        return wK + bK + wLR + wRR + bLR + bRR;
    }

    static String setOthers() {
        List<String> list = new ArrayList<>();

        final var whites = PieceType.allWhites();
        final var blacks = PieceType.allBlacks();

        for (int i = 0; i < HORIZONTAL_BOUND; i++) {
            int pick = random(0, whites.size() - 1);

            String whitePiece         = whites.get(pick).code;
            String whitePiecePosition = new Position(VERTICAL_BOUND - 1, i).getChessPosition();

            list.add(" " + whitePiece + whitePiecePosition);

            String blackPiece         = blacks.get(pick).code;
            String blackPiecePosition = new Position(0, i).getChessPosition();

            list.add(" " + blackPiece + blackPiecePosition);
        }

        StringBuilder sb = new StringBuilder();

        for (String s: list) {
            sb.append(s);
        }

        return sb.toString();
    }
}
