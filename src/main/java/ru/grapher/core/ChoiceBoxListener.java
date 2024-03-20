package ru.grapher.core;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class ChoiceBoxListener implements ListDataListener {
    private final JComboBox<String> owner;

    public ChoiceBoxListener(JComboBox<String> owner) {
        this.owner = owner;
    }

    @Override
    public void intervalAdded(ListDataEvent e) {
        update();
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
        update();
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
        update();
    }

    private void update() {
        this.owner.setEnabled(this.owner.getModel().getSize() != 0);
        onChange();
    }

    public void onChange() {

    }
}
