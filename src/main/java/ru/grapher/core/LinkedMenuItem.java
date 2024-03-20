package ru.grapher.core;

import ru.grapher.GUI;

import javax.swing.*;

public abstract class LinkedMenuItem extends JMenuItem {

    final JFrame owner;

    public LinkedMenuItem(JFrame owner, String text) {
        super(text);

        this.owner = owner;

        this.setFont(GUI.font(22));
    }
}
