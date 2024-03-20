package ru.grapher.menu;

import ru.grapher.GUI;

import javax.swing.*;

public class MainMenu extends JMenu {

    public MainMenu(JFrame owner) {
        super("Menu ");

        this.setFont(GUI.font(22));
        this.getPopupMenu().setBorder(GUI.__UNIVERSAL_BORDER);

        this.add(new HelpMenuItem(owner));
        this.add(new AboutMenuItem(owner));
        this.addSeparator();
        this.add(new ExitMenuItem(owner));
    }
}
