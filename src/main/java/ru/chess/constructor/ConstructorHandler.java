package ru.chess.constructor;

import ru.chess.Chess;
import ru.chess.gui.Board;
import ru.chess.label.DynamicLabel;
import ru.chess.gui.ImageReader;

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
                ImageReader.getPlayIcon(Board.DIMENSION_CELL.width, Board.DIMENSION_CELL.height),
                e -> play()
        );

        this.add(play);

        DynamicLabel copy = new DynamicLabel(
                this.getBackground(),
                Color.BLUE.darker(),
                ImageReader.getCopyIcon(Board.DIMENSION_CELL.width, Board.DIMENSION_CELL.height),
                e -> Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                        new StringSelection(model.getTextField().getText()),
                        (clipboard, contents) -> {

                        }
                )
        );

        this.add(copy);

        DynamicLabel reset = new DynamicLabel(
                this.getBackground(),
                Color.YELLOW,
                ImageReader.getReplayIcon(Board.DIMENSION_CELL.width, Board.DIMENSION_CELL.height),
                e -> model.reset()
        );

        this.add(reset);

        DynamicLabel exit = new DynamicLabel(
                this.getBackground(),
                Color.RED,
                ImageReader.getExitIcon(Board.DIMENSION_CELL.width, Board.DIMENSION_CELL.height),
                e -> SwingUtilities.getWindowAncestor((Component) e.getSource()).dispose()
        );

        this.add(exit);


    }

    private void play() {
        Chess chess = new Chess(model.vertical, model.horizontal, model.getTextField().getText());

        chess.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chess.setVisible(true);
    }
}
