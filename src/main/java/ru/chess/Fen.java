package ru.chess;

import ru.chess.label.Cell;
import ru.chess.model.AbstractModel;
import ru.chess.model.Model;

public final class Fen {

    public static String toFen(AbstractModel model) {
        return toFen(model, false, "-");
    }

    public static String toFen(AbstractModel model, boolean turn) {
        return toFen(model, turn, "-");
    }

    public static String toFen(AbstractModel model,
                               boolean turn,
                               String castling) {
        var cells = model.getBoard().cells;

        StringBuilder fen = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            int emptyCount = 0;

            for (int j = 0; j < 8; j++) {
                if (cells[i][j].pieceType == PieceType.NONE) {
                    emptyCount++;

                } else {
                    if (emptyCount > 0) {
                        fen.append(emptyCount);
                        emptyCount = 0;
                    }

                    fen.append(cells[i][j].pieceType.fenCode);
                }
            }

            if (emptyCount > 0)
                fen.append(emptyCount);

            if (i < 7)
                fen.append('/');
        }

        fen.append(" ").append(turn ? "w" : "b");
        fen.append(" ").append(castling.isEmpty() ? "-" : castling);
        fen.append(" ").append("-");
        fen.append(" ").append(model instanceof Model
                ? ((Model) model).fiftyRuleCounter
                : "0");

        fen.append(" ").append(model instanceof Model
                ? ((Model) model).history.size() - 1
                : "1");

        return fen.toString();
    }
}
