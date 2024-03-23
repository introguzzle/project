package ru.life;

import java.awt.*;

public class Run {
    public static void main(String[] args) {
        // Really slow when size is above 100-120
        EventQueue.invokeLater(() -> new Life(101).setVisible(true));
    }
}
