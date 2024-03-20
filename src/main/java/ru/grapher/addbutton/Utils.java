package ru.grapher.addbutton;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class Utils {
    public static boolean isParsable(String string) {
        try {
            Double.parseDouble(string);
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }

        return true;
    }

    public static void requestAttention(JComponent component) {
        class ColorHolder {
            private static final Color R = Color.GRAY;
        }

        Toolkit.getDefaultToolkit().beep();

        Color old = component.getBackground();

        component.setBackground(ColorHolder.R);

        Timer timer = new Timer(50, (e) -> {
            ((Timer) e.getSource()).stop();
            component.setBackground(old);
        });

        timer.start();
    }
}
