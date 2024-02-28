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

                if (!t.code.isEmpty()) {
                    pieceSetup.append(t.code).append(getPositionEncoded(p)).append(" ");
                }
            }

        return pieceSetup.toString();
    }

    static String getPositionEncoded(Position position) {
        return position.getChessPosition();
    }
}
