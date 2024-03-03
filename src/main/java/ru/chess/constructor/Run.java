package ru.chess.constructor;

import java.awt.*;

public class Run {

    public static void main(String... a) {
        EventQueue.invokeLater(() -> new Constructor(8, 8).setVisible(true));
    }
}
