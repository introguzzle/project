package ru.life;

import javax.swing.*;
import java.awt.*;

public class Life extends JFrame {

    public Life(int size) {
        super("Life");

        this.setLayout(new FlowLayout());

        Model model = new Model(this, size);

        this.add(model.getBoard());
        this.add(new HandleBoard(model));

        pack();

        initFrame();
    }

    private void initFrame() {
        this.setResizable(false);
        this.setAlwaysOnTop(true);
        this.setLocationRelativeTo(null);
    }
}
