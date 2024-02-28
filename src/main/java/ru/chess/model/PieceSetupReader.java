package ru.chess.model;

import ru.chess.PieceType;
import ru.chess.position.Position;

public final class PieceSetupReader {

    static String read(Model model) {
        StringBuilder pieceSetup = new StringBuilder();

        for (int i = 0; i < model.getBoard().cells.length; i++)
            for (int j = 0; j < model.getBoard().cells[i].length; j++) {

                PieceType t = model.getBoard().cells[i][j].pieceType;
                Position  p = model.getBoard().cells[i][j].position;

                if (!getPieceTypeEncoded(t).isEmpty()) {
                    pieceSetup.append(getPieceTypeEncoded(t)).append(getPositionEncoded(p)).append(" ");
                }
            }

        return pieceSetup.toString();
    }

    public static String getPieceTypeEncoded(PieceType pieceType) {
        return switch (pieceType) {
            case NONE           -> "";

            case WHITE_PAWN     -> "wp";
            case WHITE_ROOK     -> "wr";
            case WHITE_KNIGHT   -> "wk";
            case WHITE_BISHOP   -> "wb";
            case WHITE_QUEEN    -> "wq";
            case WHITE_CLOWN    -> "wc";
            case WHITE_WIZARD   -> "ww";
            case WHITE_TAMPLIER -> "wt";
            case WHITE_KING     -> "wK";

            case BLACK_PAWN     -> "bp";
            case BLACK_ROOK     -> "br";
            case BLACK_KNIGHT   -> "bk";
            case BLACK_BISHOP   -> "bb";
            case BLACK_QUEEN    -> "bq";
            case BLACK_CLOWN    -> "bc";
            case BLACK_WIZARD   -> "bw";
            case BLACK_TAMPLIER -> "bt";
            case BLACK_KING     -> "bK";
        };
    }

    static String getPositionEncoded(Position position) {
        return position.getChessPosition();
    }
}
