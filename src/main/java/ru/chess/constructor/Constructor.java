package ru.chess.constructor;

import ru.chess.PieceType;
import ru.chess.gui.ImageReader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class Constructor extends JFrame {

    private ConstructorModel model;

    public Constructor() {
        this(8, 8);
    }

    public Constructor(int vertical, int horizontal) {
        this(vertical, horizontal, "");
    }

    public Constructor(int vertical, int horizontal, String preset) {
        init(vertical, horizontal, preset);
    }

    private void init(int vertical, int horizontal, String preset) {
        this.setLayout(new FlowLayout());

        JPanel topLayer = new JPanel();

        {
            topLayer.setLayout(new FlowLayout());
            this.model = new ConstructorModel(vertical, horizontal);

            topLayer.add(this.model.getBoard());
            topLayer.add(this.model.getPanel());

            topLayer.setBackground(Color.DARK_GRAY);
        }

        JPanel nextLayer = new JPanel();

        {
            nextLayer.setLayout(new BorderLayout(0, 4));
            nextLayer.add(topLayer, BorderLayout.NORTH);

            this.getModel().getTextField().setPreferredSize(new Dimension(0, 60));
            this.getModel().getFenTextField().setPreferredSize(new Dimension(0, 60));

            this.getModel().getTextField().setCaretPosition(0);
            this.getModel().getFenTextField().setCaretPosition(0);

            nextLayer.add(this.getModel().getTextField(),    BorderLayout.CENTER);
            nextLayer.add(this.getModel().getFenTextField(), BorderLayout.SOUTH);
        }

        this.addWindowListener(createWindowListener());

        this.add(new ConstructorHandler(model));
        this.add(nextLayer);

        this.model.loadPreset(preset);

        this.pack();

        this.setTitle("Constructor");
        this.setIconImage(ImageReader.get(PieceType.BLACK_KING).getImage());

        this.setAlwaysOnTop(true);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        IO.load(model);
    }

    private WindowListener createWindowListener() {
        return new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                IO.write(model);
            }
        };
    }

    ConstructorModel getModel() {
        return this.model;
    }
}
