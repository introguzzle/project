package ru.grapher.core;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class DocumentChangeListener implements DocumentListener {
    // this field is probably is actually Consumer<DocumentEvent>, but
    // event argument is useless 99% of the time so far
    private final Runnable action;

    public DocumentChangeListener(Runnable action) {
        this.action = action;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        action.run();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        action.run();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        action.run();
    }
}
