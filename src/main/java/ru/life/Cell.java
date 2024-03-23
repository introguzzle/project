package ru.life;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class Cell extends JLabel {

    private static final Border BORDER = BorderFactory.createLineBorder(Color.BLACK, 1);

    final int i;
    final int j;

    private boolean alive = false;

    /**
     * 1  - Kill.<br>
     * 0  - Do nothing.<br>
     * -1 - Revive.
     */
    private int     flag  = 0;

    public Cell(Dimension dimension, int i, int j) {
        super();

        this.setPreferredSize(dimension);

        this.setBorder(BORDER);
        this.setOpaque(true);
        this.kill();

        this.i = i;
        this.j = j;
    }

    public boolean isAlive() {
        return this.alive;
    }

    public void invert() {
        if (alive)
            kill();
        else
            revive();
    }

    public void revive() {
        this.alive = true;
        this.flag = 0;

        this.setBackground(GUI.ALIVE_COLOR);
    }

    public void kill() {
        this.alive = false;
        this.flag = 0;

        this.setBackground(GUI.DEAD_COLOR);
    }

    public void flag(boolean shouldKill) {
        this.flag = shouldKill ? 1 : -1;
    }

    public int getFlag() {
        return this.flag;
    }
}
