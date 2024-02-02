package ru.grapher;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class Main {

    static MathGeneratorV1 g = new MathGeneratorV1();

    public static void getInfo() {
        var a = UIManager.getInstalledLookAndFeels();

        for (int i = 0; i < a.length; i++) {
            System.out.println(a[i].getName());
        }

        System.out.println("--------------------------");

        var b = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

        for (int i = 0; i < b.length; i++) {
            System.out.println(b[i]);
        }
    }

    public static void main(String... __) {

    }
}


