package ru.chess.gui;

import ru.chess.position.Position;
import ru.chess.cell.BlackCell;
import ru.chess.cell.Cell;
import ru.chess.cell.WhiteCell;

import javax.swing.*;
import java.awt.*;

public class Board extends JPanel {

    public static int HORIZONTAL_BOUND;
    public static int VERTICAL_BOUND;

    public static Dimension DIMENSION_CELL   = null;

    public static Dimension DIMENSION_WINDOW = null;

    public Cell[][]  cells;

    public ImageIcon activePieceImage;
    public Point     point;
    public boolean   isMouseDragging;

    public Board(int vertical, int horizontal) {
        VERTICAL_BOUND   = vertical;
        HORIZONTAL_BOUND = horizontal;

        DIMENSION_CELL = GUI.Adapter.getFittingCellDimension();

        DIMENSION_WINDOW = new Dimension(
                (int) (DIMENSION_CELL.getWidth()  * HORIZONTAL_BOUND),
                (int) (DIMENSION_CELL.getHeight() * VERTICAL_BOUND));

        this.cells = new Cell[VERTICAL_BOUND][HORIZONTAL_BOUND];

        init();
    }

    public Cell getCell(Position position) {
        return this.cells[position.getHeight()][position.getWidth()];
    }

    private Cell[][] createCellMatrix() {
        Cell[][] cells = new Cell[VERTICAL_BOUND][HORIZONTAL_BOUND];

        for (int h = 0; h < VERTICAL_BOUND; h++) {
            for (int w = 0; w < HORIZONTAL_BOUND; w++) {
                if (Math.floorMod(h, 2) == 0)
                    cells[h][w] = Math.floorMod(w, 2) == 0
                            ? new WhiteCell(new Position(h, w))
                            : new BlackCell(new Position(h, w));
                else
                    cells[h][w] = Math.floorMod(w, 2) == 1
                            ? new WhiteCell(new Position(h, w))
                            : new BlackCell(new Position(h, w));
            }
        }

        return cells;
    }

    private void init() {
        this.setPreferredSize(DIMENSION_WINDOW);
        this.setLayout(new GridLayout(VERTICAL_BOUND, HORIZONTAL_BOUND));

        this.cells = createCellMatrix();

        for (Cell[] cellArray : this.cells) {
            for (Cell cell : cellArray) {
                this.add(cell);
            }
        }
    }

    public void paintFontOutline(Graphics2D g2d,
                                 String     string,
                                 int        fx,
                                 int        fy,
                                 int        outlineWidth,
                                 Color      outlineColor) {
        Color oldColor = g2d.getColor();
        Font  oldFont  = g2d.getFont();

        g2d.setColor(outlineColor);
        g2d.setFont(new Font(oldFont.getFontName(), oldFont.getStyle(), oldFont.getSize() + outlineWidth));
        g2d.drawString(string, fx, fy);

        g2d.setColor(oldColor);
        g2d.setFont(oldFont);
    }

    public void paintFontShadow(Graphics2D g2d,
                                String     string,
                                int        fx,
                                int        fy,
                                int        shadowWidth,
                                Color      shadowColor) {
        Color old = g2d.getColor();

        g2d.setColor(shadowColor);
        g2d.drawString(string, fx + shadowWidth, fy);
        g2d.drawString(string, fx, fy + shadowWidth);
        g2d.drawString(string, fx - shadowWidth, fy);
        g2d.drawString(string, fx, fy - shadowWidth );

        g2d.setColor(old);
    }

    public void paintNotation(Graphics2D g2d) {
        GUI.setQuality(g2d, 2);

        Font font = GUI.Adapter.getFittingFont();

        g2d.setFont(font);

        for (int i = 0; i < VERTICAL_BOUND; i++) {
            Position p = new Position(i, 0);
            Cell cell = this.getCell(p);

            int dx = cell.getLocation().x + 2;
            int dy = cell.getLocation().y + DIMENSION_CELL.height / 3;

            String s = p.getChessPosition().substring(1);

            g2d.setColor(cell instanceof WhiteCell ? GUI.Cell.BLACK_COLOR : GUI.Cell.WHITE_COLOR);
            Color shadowColor = cell instanceof WhiteCell
                    ? GUI.Cell.WHITE_COLOR.darker().darker()
                    : GUI.Cell.BLACK_COLOR.darker().darker();

            this.paintFontShadow(g2d, s, dx, dy, 1, shadowColor);
            g2d.drawString(s, dx, dy);
        }


        for (int i = 0; i < HORIZONTAL_BOUND; i++) {
            Position p = new Position(VERTICAL_BOUND - 1, i);
            Cell cell = this.getCell(p);

            int dx = cell.getLocation().x + DIMENSION_CELL.width * 3 / 4;
            int dy = cell.getLocation().y + DIMENSION_CELL.height * 12 / 13;

            String s = p.getChessPosition().substring(0, 1);

            g2d.setColor(cell instanceof WhiteCell ? GUI.Cell.BLACK_COLOR : GUI.Cell.WHITE_COLOR);
            Color shadowColor = cell instanceof WhiteCell
                    ? GUI.Cell.WHITE_COLOR.darker().darker()
                    : GUI.Cell.BLACK_COLOR.darker().darker();

            this.paintFontShadow(g2d, s, dx, dy, 1, shadowColor);
            g2d.drawString(p.getChessPosition().substring(0, 1), dx, dy);
        }
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        GUI.setQuality(g2d, 2);

        super.paint(g2d);

        if (isMouseDragging) {
            Image pieceImage = activePieceImage.getImage();

            int dx = point.x - pieceImage.getHeight(this) / 2;
            int dy = point.y - pieceImage.getWidth(this) / 2;

            g2d.drawImage(pieceImage, dx, dy, this);
        }

        this.paintNotation(g2d);
    }
}
