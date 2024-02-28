package ru.chess.model;

public final class PresetReader {

    private PresetReader() {

    }

    static String read(Model model) {
        StringBuilder preset     = new StringBuilder(readState(model) + " / ");
        String        pieceSetup = PieceSetupReader.read(model);

        preset.append(pieceSetup);

        return preset.toString();
    }

    static String readState(Model model) {

        return String.valueOf(switch (model.state) {
            case ONGOING            -> 'O';
            case CHECKMATE_TO_WHITE -> 'W';
            case CHECKMATE_TO_BLACK -> 'B';
            case STALEMATE          -> 'S';
            default                 -> throw new IllegalArgumentException();

        }) +
                (model.turn ? 'W' : 'B') +

                (model.whiteKingMoved      ? 'T' : 'F') +
                (model.whiteLeftRookMoved  ? 'T' : 'F') +
                (model.whiteRightRookMoved ? 'T' : 'F') +

                (model.blackKingMoved      ? 'T' : 'F') +
                (model.blackLeftRookMoved  ? 'T' : 'F') +
                (model.blackRightRookMoved ? 'T' : 'F') ;
    }
}
