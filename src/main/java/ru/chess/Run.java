package ru.chess;;

public class Run {

    public static void main(String... ___) {
        //java.awt.EventQueue.invokeLater(() -> new Chess().setVisible(true));
        java.awt.EventQueue.invokeLater(() -> {
            new Chess(8, 8, "bKa4 brd4 bpd3 wpa2 wra1 wKd1").setVisible(true);
        });
    }
}
