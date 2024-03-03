package ru.chess;

import ru.chess.gui.ImageReader;
import ru.chess.model.Model;
import ru.utils.ObjectPrinter;

import javax.swing.*;
import java.awt.*;

public class Chess extends JFrame {

    private Model model;

    public Chess() {
        this(8, 8, null);
    }

    public Chess(int vertical, int horizontal) {
        this(vertical, horizontal, null);
    }

    public Chess(int vertical, int horizontal, String preset) {
        init(vertical, horizontal, preset);
    }

    private void init(int vertical, int horizontal, String preset) {
        this.setLayout(new FlowLayout());

        model = new Model(vertical, horizontal);

        if (preset == null || preset.isEmpty())
            model.loadDefaultPreset();
        else
            model.loadPreset(preset);

        this.add(model.getBoard());
        this.pack();

        this.setTitle("Chess");
        this.setIconImage(ImageReader.get(PieceType.BLACK_KING).getImage());

        this.setAlwaysOnTop(true);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    public Model getModel() {
        return this.model;
    }
}
