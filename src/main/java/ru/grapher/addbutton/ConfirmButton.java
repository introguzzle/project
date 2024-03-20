package ru.grapher.addbutton;

import ru.grapher.GUI;
import ru.grapher.core.DynamicButton;

import java.awt.*;

public class ConfirmButton extends DynamicButton {
    static final Color T = GUI.COLOR_WE_WILL_LIVE;
    static final Color F = GUI.COLOR_DEATH;

    public ConfirmButton() {
        super("Confirm", 20);

        this.setEnabled(false);
    }

    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        this.setForeground(b ? T : F);
    }
}
