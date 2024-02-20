package ru.chess.model;

import ru.chess.model.Model.State;

public interface PresetLoader extends PieceSetupLoader {

    String DEFAULT = "OWFFFFFF / wpa2 wpb2 wpc2 wpd2 wpe2 wpf2 wpg2 wph2 " +
            "bpa7 bpb7 bpc7 bpd7 bpe7 bpf7 bpg7 bph7 " +
            "wra1 wkb1 wbc1 wqd1 wKe1 wbf1 wkg1 wrh1 " +
            "bra8 bkb8 bbc8 bqd8 bKe8 bbf8 bkg8 brh8";

    static void load(Model model, String preset) {

        String[] presetParts = preset.split(" / ");

        String stateSetup = presetParts[0];
        String pieceSetup = presetParts[1];

        loadState(model, stateSetup);
        PieceSetupLoader.load(model, pieceSetup);

    }

    private static void loadState(Model model, String stateSetup) {
        State   state = switch(stateSetup.charAt(0)) {
            case 'O' -> State.ONGOING;
            case 'W' -> State.CHECKMATE_TO_WHITE;
            case 'B' -> State.CHECKMATE_TO_BLACK;
            case 'S' -> State.STALEMATE;

            default  -> throw new IllegalStateException("Unexpected value: " + stateSetup.charAt(0));
        };

        boolean turn                = stateSetup.charAt(1) == 'W';

        boolean whiteKingMoved      = stateSetup.charAt(2) == 'T';
        boolean whiteLeftRookMoved  = stateSetup.charAt(3) == 'T';
        boolean whiteRightRookMoved = stateSetup.charAt(4) == 'T';

        boolean blackKingMoved      = stateSetup.charAt(5) == 'T';
        boolean blackLeftRookMoved  = stateSetup.charAt(6) == 'T';
        boolean blackRightRookMoved = stateSetup.charAt(7) == 'T';
        
        model.state = state;
        model.turn  = turn;

        model.whiteKingMoved        = whiteKingMoved;
        model.whiteLeftRookMoved    = whiteLeftRookMoved;
        model.whiteRightRookMoved   = whiteRightRookMoved;

        model.blackKingMoved        = blackKingMoved;
        model.blackLeftRookMoved    = blackLeftRookMoved;
        model.blackRightRookMoved   = blackRightRookMoved;
    }

}
