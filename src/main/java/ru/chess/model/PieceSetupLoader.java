package ru.chess.model;

import ru.chess.PieceType;
import ru.chess.Position;

public interface PieceSetupLoader {

    static void load(Model model, String pieceSetup) {
        String[] parts = pieceSetup.split(" ");

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                model.setPiece(new Position(i, j), PieceType.NONE);

        for (String part: parts) {
            Position  position  = getPosition(part);
            PieceType pieceType = getPieceType(part);

            model.setPiece(position, pieceType);
        }
    }

    private static Position getPosition(String part) {
        return new Position(part.substring(2));
    }

    private static PieceType getPieceType(String part) {
        if (part.charAt(0) == 'w') {

            return switch (part.charAt(1)) {
                case 'p' -> PieceType.WHITE_PAWN;
                case 'r' -> PieceType.WHITE_ROOK;
                case 'b' -> PieceType.WHITE_BISHOP;
                case 'k' -> PieceType.WHITE_KNIGHT;
                case 'K' -> PieceType.WHITE_KING;
                case 'q' -> PieceType.WHITE_QUEEN;
                default  -> PieceType.NONE;
            };

        } else {

            return switch (part.charAt(1)) {
                case 'p' -> PieceType.BLACK_PAWN;
                case 'r' -> PieceType.BLACK_ROOK;
                case 'b' -> PieceType.BLACK_BISHOP;
                case 'k' -> PieceType.BLACK_KNIGHT;
                case 'K' -> PieceType.BLACK_KING;
                case 'q' -> PieceType.BLACK_QUEEN;
                default  -> PieceType.NONE;
            };
        }
    }
}
