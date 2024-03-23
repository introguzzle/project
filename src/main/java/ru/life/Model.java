package ru.life;

import ru.life.io.IO;
import ru.life.io.IOSupport;

import javax.swing.*;
import java.awt.*;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Predicate;

public class Model {

    private final JFrame owner;

    final int size;
    private final Board board;

    private Timer timer;

    public Model(JFrame owner, int size) {
        this.size = size;

        this.owner = owner;
        this.board = new Board(size, autoDimension());

        Listener listener = new Listener(this);

        this.board.addMouseListener(listener);
        this.board.addMouseMotionListener(listener);
    }

    public void next() {
        for (Cell c: board) flag(c);
        for (Cell c: board) apply(c);
        board.repaint();
        board.value++;
    }

    private void apply(Cell cell) {
        if (cell.getFlag() == 1)
            cell.kill();

        else if (cell.getFlag() == -1)
            cell.revive();
    }

    private void flag(Cell cell) {
        int aliveCount = countAlive(cell);

        if (!cell.isAlive()) {
            if (aliveCount == 3)
                cell.flag(false);

        } else {
            if (aliveCount < 2 || aliveCount > 3)
                cell.flag(true);
        }
    }

    private int countAlive(Cell cell) {
        int count = 0;

        List<Cell> around = getAround(cell);

        for (Cell c: around)
            if (c.isAlive())
                count++;

        return count;
    }

    private List<Cell> getAround(Cell cell) {
        List<Cell> cells = new ArrayList<>();

        for (int sy = -1; sy <= 1; sy++)
            for (int sx = -1; sx <= 1; sx++) {
                if (sy == 0 && sx == 0)
                    continue;

                int y = cell.i + sy;
                int x = cell.j + sx;

                if (y >= 0 && y < size && x >= 0 && x < size)
                    cells.add(board.cells[y][x]);
            }

        return cells;
    }

    public void fill(Predicate<Cell> filter) {
        for (Cell c: board) if (filter.test(c)) c.revive();
        else c.kill();

        board.value = board.playing ? board.value : 0;
    }

    public void fillRandom(double factor) {
        fill((cell) -> Math.random() > Math.abs(factor));
    }

    public void fillRandom() {
        fillRandom(Math.random());
    }

    private Dimension autoDimension() {
        int d = GUI.FRAME_HEIGHT / this.size;
        int bound = 40;

        return d > bound ? new CellDimension(bound) : new CellDimension(d);
    }

    public Board getBoard() {
        return this.board;
    }

    public void play(int delay) {
        if (timer == null) {
            timer = new Timer(delay, e -> next());

            timer.start();
        } else {
            timer.setDelay(delay);
        }

        board.playing = true;
        board.repaint();
    }

    public void stop() {
        if (timer != null) timer.stop();
        timer = null;

        board.playing = false;
        board.repaint();
    }

    public void clear() {
        for (Cell c: board)
            c.kill();

        board.value = 0;
    }

    public void write() {
        IO.write(IOSupport.toMatrix(this));
    }

    public void reset() {
        try {
            board.setAll(IO.load(size, size));
        } catch (Exception ignored) {

        }

        board.value = 0;
    }

    public void save() {
        IO.save(owner, IOSupport.toMatrix(this));
    }

    public void open() {
        boolean[][] matrix = IO.open(owner, size, size);

        if (matrix != null)
            board.setAll(matrix);

        board.value = 0;
    }

    public Cell[][] getCells() {
        return board.cells;
    }

    public int getSize() {
        return size;
    }

    public void mirror() {
        verticalMirror();
        horizontalMirror();
    }

    public void showQuarter() {
        for (int i = 0; i < size / 2; i++)
            board.cells[size / 2 + 1][i].setBackground(GUI.R);

        for (int i = 0; i < size / 2; i++) {
            Cell cell = board.cells[size / 2 + i + 1][size / 2 - 1];

            cell.kill();
            cell.setBackground(GUI.R);
        }
    }

    private void verticalMirror() {
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size / 2 ; j++)
                if (board.cells[i][j].isAlive())
                    board.cells[size - i - 1][j].revive();
    }

    private void horizontalMirror() {
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size / 2 ; j++)
                if (board.cells[i][j].isAlive())
                    board.cells[i][size - j - 1].revive();
    }
}
