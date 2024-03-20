package ru.grapher.exit;

import ru.grapher.core.ActionInvokingDialog;
import ru.grapher.core.EventListener;

import javax.swing.*;

public class ExitDialog extends ActionInvokingDialog {

    public ExitDialog(JFrame owner) {
        this(owner, () -> Runtime.getRuntime().exit(0));
    }

    public ExitDialog(JFrame owner, Runnable confirmAction) {
        super(owner, new ExitPanel(), "Exit");

        ExitPanel panel = (ExitPanel) this.getContent();

        panel.confirmButton.addActionListener(new EventListener((e) -> {
            if (confirmAction != null)
                confirmAction.run();

            this.dispose();

            owner.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            owner.dispose();
        }));

        panel.cancelButton.addActionListener(new EventListener((e) -> this.dispose()));

        this.setVisible(true);
    }

    @Override
    protected void onDone() {
        this.dispose();
    }
}