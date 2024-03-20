package ru.grapher.core;

import ru.grapher.core.LinkedPanel;
import ru.grapher.core.WindowClosingListener;

import javax.swing.*;
import java.awt.*;

public abstract class ActionInvokingDialog extends JDialog {

    private final LinkedPanel content;
    private final JFrame owner;

    public ActionInvokingDialog(JFrame owner,
                                LinkedPanel content,
                                String name) {
        super(owner, name, true);

        this.owner = owner;
        this.content = content;

        this.init();
    }

    public ActionInvokingDialog(AWTEvent event,
                                LinkedPanel content,
                                String name) {
        super(
                (JFrame) SwingUtilities.getWindowAncestor((JComponent) event.getSource()),
                name,
                true
        );

        this.owner = (JFrame) SwingUtilities.getWindowAncestor((JComponent) event.getSource());
        this.content = content;

        this.init();
    }

    private void init() {
        this.add(content);
        this.addWindowListener(new WindowClosingListener((windowEvent) -> {
            if (content.isDone())
                onDone();
        }));

        this.pack();
        this.setResizable(false);
        this.setAlwaysOnTop(true);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    protected abstract void onDone();

    public JFrame getOwner() {
        return this.owner;
    }

    public LinkedPanel getContent() {
        return this.content;
    }
}
