package ru.chess.model;

import ru.chess.PieceType;
import ru.chess.position.Position;
import ru.chess.model.Model.State;

public class Presets {

    private Presets() {

    }

    public static String DEFAULT = "OWFFFFFF / wpa2 wpb2 wpc2 wpd2 wpe2 wpf2 wpg2 wph2 " +
                "bpa7 bpb7 bpc7 bpd7 bpe7 bpf7 bpg7 bph7 " +
                "wra1 wkb1 wbc1 wqd1 wKe1 wbf1 wkg1 wrh1 " +
                "bra8 bkb8 bbc8 bqd8 bKe8 bbf8 bkg8 brh8";


    public static final class Loader {

        public static void load(AbstractModel model, String preset) {
            class PSLoader {
                static void load(AbstractModel model, String pieceSetup) {
                    if (pieceSetup == null || pieceSetup.isEmpty()) {
                        return;
                    }

                    String[] parts = pieceSetup.split(" ");

                    model.reset();

                    for (String part: parts) {
                        Position  position  = new Position(part.substring(2));
                        PieceType pieceType = PieceType.of(part.substring(0, 2));

                        model.setPiece(position, pieceType);
                    }
                }
            }

            if (model instanceof Model) {
                if (preset.contains("/")) {
                    String[] presetParts = preset.split(" / ");

                    String stateSetup = presetParts[0];
                    loadState((Model) model, stateSetup);

                    try {
                        String pieceSetup = presetParts[1];
                        PSLoader.load(model, pieceSetup);
                    } catch (ArrayIndexOutOfBoundsException ignored) {

                    }
                } else {
                    try {
                        loadState((Model) model, "OWFFFFFF");
                        PSLoader.load(model, preset);
                    } catch (ArrayIndexOutOfBoundsException ignored) {

                    }
                }
            } else {
                PSLoader.load(model, preset);
            }
        }

        static void loadState(Model model, String stateSetup) {
            Model.State state = switch(stateSetup.charAt(0)) {
                case 'O' -> Model.State.ONGOING;
                case 'W' -> Model.State.CHECKMATE_TO_WHITE;
                case 'B' -> Model.State.CHECKMATE_TO_BLACK;
                case 'S' -> Model.State.STALEMATE;

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

    public static final class Reader {

        public static String read(AbstractModel model) {
            class PSReader {
                static String read(AbstractModel model) {
                    StringBuilder pieceSetup = new StringBuilder();

                    for (int i = 0; i < model.getBoard().cells.length; i++)
                        for (int j = 0; j < model.getBoard().cells[i].length; j++) {

                            PieceType t = model.getBoard().cells[i][j].pieceType;
                            Position  p = model.getBoard().cells[i][j].getPosition();

                            if (!t.code.isEmpty()) {
                                pieceSetup.append(t.code).append(p.getChessPosition()).append(" ");
                            }
                        }

                    return pieceSetup.toString();
                }
            }

            if (model instanceof Model) {
                var preset = new StringBuilder(readState((Model) model) + " / ");
                String pieceSetup = PSReader.read(model);

                preset.append(pieceSetup);

                return preset.toString();
            } else {
                return PSReader.read(model);
            }
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
}
