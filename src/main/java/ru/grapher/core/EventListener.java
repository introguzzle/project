package ru.grapher.core;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.function.Consumer;

public class EventListener implements ActionListener {
    private final Consumer<AWTEvent> action;

    public EventListener(Consumer<AWTEvent> action) {
        this.action = action;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        action.accept(e);
    }
}
