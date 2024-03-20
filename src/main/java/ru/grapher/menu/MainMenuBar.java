package ru.grapher.menu;

import ru.grapher.GUI;

import javax.swing.*;

public class MainMenuBar extends JMenuBar {

    public MainMenuBar(JFrame owner) {
        this.setFont(GUI.font(22));

        this.add(new MainMenu(owner));
    }
}
