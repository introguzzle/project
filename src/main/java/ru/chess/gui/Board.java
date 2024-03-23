package ru.chess.gui;

import ru.chess.position.Position;
import ru.chess.label.Cell;
import ru.chess.label.WhiteCell;
import ru.utils.ColorUtilities;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;

public class Board extends JPanel implements Iterable<Cell> {

    public static int HORIZONTAL_BOUND;
    public static int VERTICAL_BOUND;

    public static Dimension DIMENSION_CELL   = null;
    public static Dimension DIMENSION_WINDOW = null;

    public Cell[][]  cells;

    ImageIcon activePieceImage;
    Point     drawingPoint;
    boolean   drawPiece;

    public Board(int vertical, int horizontal) {
        super();

        VERTICAL_BOUND   = vertical;
        HORIZONTAL_BOUND = horizontal;

        DIMENSION_CELL   = GUI.Adapter.getFittingCellDimension();

        DIMENSION_WINDOW = new Dimension(
                (int) (DIMENSION_CELL.getWidth()  * HORIZONTAL_BOUND),
                (int) (DIMENSION_CELL.getHeight() * VERTICAL_BOUND)
        );

        this.cells = new Cell[VERTICAL_BOUND][HORIZONTAL_BOUND];

        init();
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

    public static Cell[][] createCellMatrix() {
        return BoardSupport.createCellMatrix(VERTICAL_BOUND, HORIZONTAL_BOUND);
    }

    private void paintNotation(Graphics2D g2d) {
        Font font = GUI.Adapter.getFittingFont(
                cells[0][0],
                g2d,
                GUI.Cell.NOTATION_FONT,
                "a1",
                40);

        g2d.setFont(font);

        for (int i = 0; i < VERTICAL_BOUND; i++) {
            Position pos  = new Position(i, 0);
            Cell     cell = this.getCell(pos);

            int dx = cell.getLocation().x + 2;
            int dy = cell.getLocation().y + DIMENSION_CELL.height / 3;

            String s = pos.getChessPosition().substring(1);

            g2d.setColor(cell instanceof WhiteCell ? GUI.Cell.BLACK_COLOR : GUI.Cell.WHITE_COLOR);
            Color shadowColor = cell instanceof WhiteCell
                    ? ColorUtilities.darken(GUI.Cell.WHITE_COLOR, 0.5)
                    : ColorUtilities.darken(GUI.Cell.BLACK_COLOR, 0.5);

            BoardDraw.paintFontOutline(g2d, s, dx, dy, 1, shadowColor);
            g2d.drawString(s, dx, dy);
        }


        for (int i = 0; i < HORIZONTAL_BOUND; i++) {
            Position pos  = new Position(VERTICAL_BOUND - 1, i);
            Cell     cell = this.getCell(pos);

            int dx = cell.getLocation().x + DIMENSION_CELL.width * 3 / 4;
            int dy = cell.getLocation().y + DIMENSION_CELL.height * 12 / 13;

            String s = pos.getChessPosition().substring(0, 1);

            g2d.setColor(cell instanceof WhiteCell ? GUI.Cell.BLACK_COLOR : GUI.Cell.WHITE_COLOR);
            Color shadowColor = cell instanceof WhiteCell
                    ? ColorUtilities.darken(GUI.Cell.WHITE_COLOR, 0.5)
                    : ColorUtilities.darken(GUI.Cell.BLACK_COLOR, 0.5);

            BoardDraw.paintFontOutline(g2d, s, dx, dy, 1, shadowColor);
            g2d.drawString(pos.getChessPosition().substring(0, 1), dx, dy);
        }
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHints(GUI.Q_RENDERING_HINTS);

        super.paint(g2d);

        if (drawPiece) {
            Image pieceImage = activePieceImage.getImage();

            int dx = drawingPoint.x - pieceImage.getHeight(this) / 2;
            int dy = drawingPoint.y - pieceImage.getWidth(this) / 2;

            g2d.drawImage(pieceImage, dx, dy, this);
        }

        this.paintNotation(g2d);
    }

    @Override
    public Iterator<Cell> iterator() {
        return new It();
    }

    private class It implements Iterator<Cell> {
        int h = 0;
        int w = 0;

        @Override
        public boolean hasNext() {
            return h < VERTICAL_BOUND && w < HORIZONTAL_BOUND;
        }

        @Override
        public Cell next() {
            Cell nextCell = cells[h][w];

            w++;
            reset();

            return nextCell;
        }

        private void reset() {
            if (w >= HORIZONTAL_BOUND) {
                w = 0;
                h++;
            }
        }
    }

    public Cell getCell(Position position) {
        return this.cells[position.getHeight()][position.getWidth()];
    }

    public ImageIcon getActivePieceImage() {
        return activePieceImage;
    }

    public void setActivePieceImage(ImageIcon activePieceImage) {
        this.activePieceImage = activePieceImage;
    }

    public Point getDrawingPoint() {
        return drawingPoint;
    }

    public void setDrawingPoint(Point drawingPoint) {
        this.drawingPoint = drawingPoint;
    }

    public void setDrawPiece(boolean drawPiece) {
        this.drawPiece = drawPiece;
    }
}
