package com.proj;

import com.mathp.MathParser;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        JFrame frame = new JFrame("My First GUI"); // Для окна нужна "рама" - Frame
        // стандартное поведение при закрытии окна - завершение приложения
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 300); // размеры окна
        frame.setLocationRelativeTo(null); // окно - в центре экрана
        JButton button = new JButton("Press"); // Экземпляр класса JButton
        // getContentPane() - клиентская область окна
        frame.getContentPane().add(button); // Добавляем кнопку на Frame
        frame.setVisible(true); // Делаем окно видимым
    }

    public static String ULTRA_MAGIC_REPLACE(String SOURCE, char K, double value) {
        int count = 0;
        StringBuilder _split = new StringBuilder(SOURCE);
        String splitter = "_";

        for (int i = 0; i < SOURCE.length(); i++) {
            String current = Character.toString(_split.charAt(i));
            char left = (i != 0) ? _split.charAt(i - 1) : ' ';
            char right = (i != SOURCE.length() - 1) ? _split.charAt(i + 1) : ' ';

            if ((current.equals(Character.toString(K))) && (!(Character.isLetter(left))) && (!(Character.isLetter(right)))) {
                count++;
            }
        }

        for (int i = 0; i < count * 2; i++) {
            _split.append(" ");
        }

        for (int i = 0; i < SOURCE.length() + count * 2; i++) {
            String current = Character.toString(_split.charAt(i));
            char left = (i != 0) ? _split.charAt(i - 1) : ' ';
            char right = (i != SOURCE.length() - 1) ? _split.charAt(i + 1) : ' ';

            if ((current.equals(Character.toString(K))) && (!(Character.isLetter(left))) && (!(Character.isLetter(right)))) {
                _split.insert(i, "y");
            }
        }



        return _split.toString();
    }

}
