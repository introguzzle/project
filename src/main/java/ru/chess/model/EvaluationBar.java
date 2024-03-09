package ru.chess.model;

import javax.swing.*;
import java.awt.*;

public class EvaluationBar extends JPanel {

    public EvaluationBar() {
        super();

        init();
    }

    private void init() {
        this.setPreferredSize(new Dimension(80, 640));
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
    }

}
