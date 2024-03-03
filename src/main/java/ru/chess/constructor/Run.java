package ru.chess.constructor;

import java.awt.*;

public class Run {

    public static void main(String... a) {
        EventQueue.invokeLater(() -> new Constructor(4, 4).setVisible(true));
    }
}
