package ru.chess.model;

import ru.chess.PieceType;
import ru.chess.position.Position;

public class PieceSetupLoader {

    private PieceSetupLoader() {

    }

    static void load(Model model, String pieceSetup) {
        String[] parts = pieceSetup.split(" ");

        model.reset();

        for (String part: parts) {
            Position  position  = new Position(part.substring(2));
            PieceType pieceType = PieceType.of(part.substring(0, 2));

            model.setPiece(position, pieceType);
        }
    }
}
