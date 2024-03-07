package ru.chess.constructor;

import ru.chess.Chess;
import ru.chess.gui.Board;
import ru.chess.label.DynamicLabel;
import ru.chess.gui.ImageReader;
import ru.chess.model.PresetFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class ConstructorHandler extends JPanel {

    public final ConstructorModel model;

    public ConstructorHandler(ConstructorModel model) {
        super();

        this.model = model;

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        DynamicLabel play = new DynamicLabel(
                this.getBackground(),
                Color.GREEN.darker(),
                ImageReader.get("Play", Board.DIMENSION_CELL.width, Board.DIMENSION_CELL.height),
                e -> play()
        );

        this.add(play);

        DynamicLabel playAI = new DynamicLabel(
                this.getBackground(),
                Color.GREEN.darker(),
                ImageReader.get("OtherPlay", Board.DIMENSION_CELL.width, Board.DIMENSION_CELL.height),
                e -> playWithBot()
        );

        this.add(playAI);

        DynamicLabel setDefault = new DynamicLabel(
                this.getBackground(),
                Color.BLUE.darker(),
                ImageReader.get("SetDefault", Board.DIMENSION_CELL.width, Board.DIMENSION_CELL.height),
                e -> setDefault()
        );

        this.add(setDefault);

        DynamicLabel copy = new DynamicLabel(
                this.getBackground(),
                Color.BLUE.darker(),
                ImageReader.get("Copy", Board.DIMENSION_CELL.width, Board.DIMENSION_CELL.height),
                e -> copyToClipboard(model)
        );

        this.add(copy);

        DynamicLabel reset = new DynamicLabel(
                this.getBackground(),
                Color.YELLOW,
                ImageReader.get("Reset", Board.DIMENSION_CELL.width, Board.DIMENSION_CELL.height),
                e -> reset()
        );

        this.add(reset);

        DynamicLabel exit = new DynamicLabel(
                this.getBackground(),
                Color.RED,
                ImageReader.get("Exit", Board.DIMENSION_CELL.width, Board.DIMENSION_CELL.height),
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
        Chess chess = new Chess(
                model.vertical,
                model.horizontal,
                "OWFFFFFF / " + model.getTextField().getText(),
                -1,
                -1
        );

        chess.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chess.setVisible(true);
    }

    private void playWithBot() {
        ConstructorSettingsDialog dialog = new ConstructorSettingsDialog((JFrame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);

        if (dialog.valid) {
            Chess chess = new Chess(
                    model.vertical,
                    model.horizontal,
                    "OWFFFFFF / " + model.getTextField().getText(),
                    dialog.difficulty,
                    dialog.timeToMove
            );

            chess.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            chess.setVisible(true);
        }
    }

    private void setDefault() {
        String pieceSetup = PresetFactory.createDefaultPieceSetup();

        System.out.println(pieceSetup);

        model.loadPreset(pieceSetup);
        model.getTextField().setText(pieceSetup);
    }

    private void reset() {
        model.reset();
        model.getTextField().setText("");
    }
}
