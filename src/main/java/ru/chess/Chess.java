package ru.chess;

import ru.chess.model.Model;

import javax.swing.*;
import java.awt.*;

public class Chess extends JFrame {

    private Model model;

    public Chess() {
        init();
    }

    public void reinitialize() {
        model.clearHistory();
        model.setInitLoad(true);
        model.loadPreset(model.getInitPreset());

        model.getBoard().repaint();

    }

    private void init() {
        this.setLayout(new FlowLayout());

        model = new Model();

        model.loadPreset("OWFFFFFF / wpa7 wKb1 bKb4");
        //model.loadDefaultPreset();

        this.add(model.getBoard());

        this.pack();

        this.setTitle("Chess");
        this.setIconImage(ImageReader.get(PieceType.BLACK_KING).getImage());

        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    public static void main(String... ___) {
        EventQueue.invokeLater(() -> new Chess().setVisible(true));
    }
}
