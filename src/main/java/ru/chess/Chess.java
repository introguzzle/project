package ru.chess;

import ru.chess.gui.ImageReader;
import ru.chess.model.Model;

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
        this(vertical, horizontal, preset, -1, -1);
    }

    public Chess(int vertical,
                 int horizontal,
                 String preset,
                 int difficulty,
                 int timeForMove) {
        init(vertical, horizontal, preset, difficulty, timeForMove);
    }

    private void init(int vertical,
                      int horizontal,
                      String preset,
                      int difficulty,
                      int timeForMove) {

        this.setLayout(new FlowLayout());

        if (difficulty >= 0 && difficulty <= 20 && vertical == 8 && horizontal == 8)
            model = new Model(vertical, horizontal, difficulty, timeForMove);
        else
            model = new Model(vertical, horizontal);

        if (preset == null || preset.isEmpty())
            model.loadDefaultPreset();
        else
            model.loadPreset(preset);

        this.add(model.getEvaluationBar());
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
