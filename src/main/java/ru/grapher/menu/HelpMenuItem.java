package ru.grapher.menu;

import ru.grapher.core.EventListener;
import ru.grapher.core.LinkedMenuItem;
import ru.grapher.menuframe.HelpFrame;

import javax.swing.*;

public class HelpMenuItem extends LinkedMenuItem {
    public HelpMenuItem(JFrame owner) {
        super(owner, "Help");

        this.addActionListener(new EventListener(e -> new HelpFrame(owner).setVisible(true)));
    }
}
