package ru.chess;

import javax.swing.*;
import java.awt.*;

public class AnimationExample extends JPanel {
    private int x = 50;
    private int y = 50;
    private int velocity = 0;
    private int acceleration = 2;

    public AnimationExample() {
        new AnimationWorker().execute();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.RED);
        g.fillOval(x, y, 20, 20);
    }

    private class AnimationWorker extends SwingWorker<Void, Void> {
        @Override
        protected Void doInBackground() throws Exception {
            while (x < 300) {
                // Обновляем позицию объекта с учетом скорости и ускорения
                x += velocity;
                y += velocity;
                velocity += acceleration;

                // Перерисовываем компонент
                repaint();

                // Приостанавливаем выполнение на 10 миллисекунд
                Thread.sleep(10);
            }
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Animation Example");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(400, 400);
                frame.add(new AnimationExample());
                frame.setVisible(true);
            }
        });
    }
}
