package ru.grapher.menu;

import ru.grapher.core.EventListener;
import ru.grapher.Grapher;
import ru.grapher.core.LinkedMenuItem;
import ru.grapher.exit.ExitDialog;

import javax.swing.*;

public class ExitMenuItem extends LinkedMenuItem {
    public ExitMenuItem(JFrame owner) {
        super(owner, "Exit");

        this.addActionListener(Grapher.RunConfiguration.ASK_CONFIRMATION
                ? new EventListener(e -> new ExitDialog(owner))
                : new EventListener(e -> owner.dispose()));
    }
}
