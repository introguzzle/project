package ru.chess;

import ru.chess.gui.ImageReader;
import ru.chess.model.Model;
import ru.chess.model.Move;
import ru.chess.position.Position;
import ru.utils.ObjectPrinter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

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

        this.add(model.getBoard());
        this.add(new JButton(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.movePiece(new Move(
                                new Position(0, 0),
                                new Position(5, 7),
                                PieceType.BLACK_ROOK
                        )
                );
            }
        }));

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
