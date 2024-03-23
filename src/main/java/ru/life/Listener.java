package ru.life;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Listener extends MouseAdapter {

    private final Model model;

    Listener(Model model) {
        this.model = model;
    }

    private Cell getCellAt(MouseEvent e) {
        return (Cell) model.getBoard().getComponentAt(e.getPoint());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseDragged(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Cell cell = getCellAt(e);

        if (SwingUtilities.isLeftMouseButton(e)) {
            if (cell != null && !cell.isAlive()) cell.revive();
        } else {
            if (cell != null && cell.isAlive()) cell.kill();
        }
    }
}