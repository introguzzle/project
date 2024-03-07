package ru.chess;

import java.io.*;

;

public final class Run {

    public static void main(String... ___) throws IOException {
        java.awt.EventQueue.invokeLater(() -> {
            new Chess(8, 8).setVisible(true);
        });
    }
}
