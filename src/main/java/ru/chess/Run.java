package ru.chess;

public final class Run {
    public static void main(String... ___) {
        java.awt.EventQueue.invokeLater(() -> new Chess(8, 8).setVisible(true));
    }
}
