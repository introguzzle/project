package ru.chess;

import ru.chess.gui.ImageReader;
import ru.chess.model.Model;
import ru.utils.ObjectDump;

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

        model = new Model(9, 8);
        //model.loadDefaultPreset();
        model.loadPreset("wKa1 bKb4 wch4 wwg5 wkg4 wpa2 bpb3");

        this.add(model.getBoard());
        this.pack();

        this.setTitle("Chess");
        this.setIconImage(ImageReader.get(PieceType.BLACK_KING).getImage());

        //this.setAlwaysOnTop(true);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    public Model getModel() {
        return this.model;
    }

    public static void main(String... ___) {
        EventQueue.invokeLater(() -> new Chess().setVisible(true));
    }
}
