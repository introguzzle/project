package ru.chess.bot;

import ru.chess.PieceType;
import ru.chess.label.Cell;

public final class Fen {

    public static String toFen(Cell[][] cells) {
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

            if (emptyCount > 0) {
                fen.append(emptyCount);
            }

            if (i < 7) {
                fen.append('/');
            }
        }

        fen.append(" ").append("b");
        fen.append(" ").append("-");
        fen.append(" ").append("-");
        fen.append(" ").append("0");
        fen.append(" ").append("1");

        return fen.toString();
    }
}
