package ru.grapher.core;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class WindowClosingListener implements WindowListener {

    private final WindowAction action;

    public WindowClosingListener(WindowAction action) {
        this.action = action;
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        this.action.accept(e);
    }

    @Override
    public void windowClosed(WindowEvent e) {
        this.action.accept(e);
    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
