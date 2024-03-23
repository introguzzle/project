package ru.life;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;
import java.util.function.Consumer;

public class Board extends JPanel implements Iterable<Cell> {

    final int       size;
    final Dimension dimension;

    final Cell[][]  cells;

    boolean playing = false;
    int     value   = 0;

    public Board(int size, Dimension dimension) {
        super();

        this.dimension = dimension;
        this.size = size;

        this.cells = new Cell[size][size];

        init();
    }

    private void init() {
        this.setLayout(new GridLayout(size, size));

        initCellMatrix();

        for (Cell c: this) this.add(c);
    }

    private void initCellMatrix() {
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++) {
                cells[i][j] = new Cell(dimension, i, j);
            }
    }

    public void setAll(boolean[][] m) {
        try {
            for (int i = 0; i < size; i++)
                for (int j = 0; j < size; j++)
                    if (m[i][j])
                        cells[i][j].revive();
                    else
                        cells[i][j].kill();
        } catch (IndexOutOfBoundsException ignored) {

        }

        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2d = (Graphics2D) g;

        drawValue(g2d);
        drawStatus(g2d);

        g2d.dispose();
    }

    private void drawStatus(Graphics2D g2d) {
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );


        AlphaComposite alpha = AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER,
                0.5f
        );

        g2d.setComposite(alpha);
        g2d.setColor(playing ? GUI.G : GUI.R);

        int offset = 10;
        int size = 20;

        g2d.drawOval(offset, offset, size, size);
        g2d.fillOval(offset, offset, size, size);
    }

    private void drawValue(Graphics2D g2d) {
        g2d.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        );

        g2d.setColor(GUI.R);
        g2d.setFont(GUI.VALUE_FONT);

        FontMetrics fontMetrics = g2d.getFontMetrics();

        String str = String.valueOf(value);

        int textHeight = fontMetrics.getHeight();

        int x = 10;
        int y = getHeight() - textHeight + 20;

        g2d.drawString(str, x, y);
    }

    @Override
    public Iterator<Cell> iterator() {
        return new It();
    }

    private class It implements Iterator<Cell> {
        int i = 0;
        int j = 0;

        @Override
        public boolean hasNext() {
            return i < size && j < size;
        }

        @Override
        public Cell next() {
            Cell nextCell = cells[i][j];

            j++;
            reset();

            return nextCell;
        }

        private void reset() {
            if (j >= size) {
                j = 0;
                i++;
            }
        }
    }
}
