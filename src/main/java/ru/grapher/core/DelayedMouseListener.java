package ru.grapher.core;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class DelayedMouseListener extends MouseAdapter {
    private final Consumer<MouseEvent> action;

    public DelayedMouseListener(Consumer<MouseEvent> action) {
        this.action = action;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.action.accept(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.action.accept(e);
    }
}
