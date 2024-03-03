package ru.chess.constructor;

import ru.chess.PieceType;
import ru.chess.gui.ImageReader;
import ru.chess.position.AbstractPosition;

import javax.swing.*;
import java.awt.*;

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
            this.getModel().getTextField().setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 6));

            nextLayer.add(this.getModel().getTextField());
        }

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

        System.out.println(AbstractPosition.VERTICAL_BOUND);
        System.out.println(AbstractPosition.HORIZONTAL_BOUND);
    }

    public ConstructorModel getModel() {
        return this.model;
    }
}
