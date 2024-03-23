package ru.chess.constructor;

import ru.chess.Chess;
import ru.chess.Fen;
import ru.chess.gui.Board;
import ru.chess.label.DynamicLabel;
import ru.chess.gui.ImageReader;
import ru.chess.model.PresetFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;

class ConstructorHandler extends JPanel {

    final ConstructorModel model;

    ConstructorHandler(ConstructorModel model) {
        super();

        this.model = model;

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        DynamicLabel play = new DynamicLabel(
                this.getBackground(),
                Color.GREEN.darker(),
                ImageReader.get("Play", Board.DIMENSION_CELL),
                "Play",
                e -> play()
        );

        this.add(play);

        DynamicLabel playAI = new DynamicLabel(
                this.getBackground(),
                Color.GREEN.darker(),
                ImageReader.get("OtherPlay", Board.DIMENSION_CELL),
                "Play AI",
                e -> playWithBot()
        );

        this.add(playAI);

        DynamicLabel setDefault = new DynamicLabel(
                this.getBackground(),
                Color.BLUE.darker(),
                ImageReader.get("SetDefault", Board.DIMENSION_CELL),
                "Set",
                e -> setDefault()
        );

        this.add(setDefault);

        DynamicLabel setRandom = new DynamicLabel(
                this.getBackground(),
                Color.BLUE.darker(),
                ImageReader.get("SetRandom", Board.DIMENSION_CELL),
                "Set",
                e -> setRandom()
        );

        this.add(setRandom);

        DynamicLabel copy = new DynamicLabel(
                this.getBackground(),
                Color.YELLOW,
                ImageReader.get("Copy", Board.DIMENSION_CELL),
                "Copy",
                e -> copyToClipboard(model)
        );

        this.add(copy);

        DynamicLabel clear = new DynamicLabel(
                this.getBackground(),
                Color.YELLOW,
                ImageReader.get("Reset", Board.DIMENSION_CELL),
                "Clear",
                e -> clear()
        );

        this.add(clear);

        DynamicLabel restore = new DynamicLabel(
                this.getBackground(),
                Color.YELLOW,
                ImageReader.get("Back", Board.DIMENSION_CELL),
                "Restore",
                e -> restore()
        );


        this.add(restore);

        DynamicLabel exit = new DynamicLabel(
                this.getBackground(),
                Color.RED,
                ImageReader.get("Exit", Board.DIMENSION_CELL),
                "Exit",
                e -> SwingUtilities.getWindowAncestor((Component) e.getSource()).dispose()
        );

        this.add(exit);
    }

    private void copyToClipboard(ConstructorModel model) {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                new StringSelection(model.getTextField().getText()),
                (clipboard, contents) -> {

                }
        );
    }

    private void play() {
        EventQueue.invokeLater(() -> {
            Chess chess = new Chess(
                    model.vertical,
                    model.horizontal,
                    "OWFFFFFF / " + model.getTextField().getText()
            );

            chess.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            chess.setVisible(true);
        });
    }

    private void playWithBot() {
        ConstructorSettingsDialog dialog = new ConstructorSettingsDialog((JFrame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);

        if (dialog.valid) {
            EventQueue.invokeLater(() -> {
                Chess chess = new Chess(
                        model.vertical,
                        model.horizontal,
                        "OWFFFFFF / " + model.getTextField().getText(),
                        dialog.difficulty,
                        dialog.timeToMove
                );

                chess.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                chess.setVisible(true);
            });
        }
    }

    private void setDefault() {
        String pieceSetup = PresetFactory.createDefaultPieceSetup();

        model.loadPreset(pieceSetup);
        model.getTextField().setText(pieceSetup);

        model.getFenTextField().setText(Fen.toFen(model, true));

        model.getBoard().repaint();
    }

    private void setRandom() {
        String pieceSetup = PresetFactory.createDefaultRandomPieceSetup();

        model.loadPreset(pieceSetup);
        model.getTextField().setText(pieceSetup);

        model.getFenTextField().setText(Fen.toFen(model, true));

        model.getBoard().repaint();
    }

    private void clear() {
        model.reset();
        model.getTextField().setText("");

        model.getFenTextField().setText("8/8/8/8/8/8/8/8 w - - 0 1");

        model.getBoard().repaint();
    }

    private void restore() {
        IO.load(model);

        model.getBoard().repaint();
    }
}
