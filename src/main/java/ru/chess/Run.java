package ru.chess;;

public class Run {

    public static void main(String... ___) {
        //java.awt.EventQueue.invokeLater(() -> new Chess().setVisible(true));
        java.awt.EventQueue.invokeLater(() -> {
            new Chess(8, 8, "bKa8 bqc7 wrc6 wKc3  ").setVisible(true);
        });
    }
}
