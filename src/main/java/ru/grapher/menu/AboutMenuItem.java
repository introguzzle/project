package ru.grapher.menu;

import ru.grapher.core.EventListener;
import ru.grapher.core.LinkedMenuItem;
import ru.grapher.menuframe.AboutFrame;

import javax.swing.*;

public class AboutMenuItem extends LinkedMenuItem {
    public AboutMenuItem(JFrame owner) {
        super(owner, "About");

        this.addActionListener(new EventListener(e -> new AboutFrame(owner).setVisible(true)));
    }
}
