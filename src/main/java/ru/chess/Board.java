package ru.chess;

import ru.chess.cell.BlackCell;
import ru.chess.cell.Cell;
import ru.chess.cell.WhiteCell;

import javax.swing.*;
import java.awt.*;

public class Board extends JPanel {

    public static final Dimension DIMENSION_CELL   = new Dimension(80, 80);
    public static final Dimension DIMENSION_WINDOW = new Dimension(
            (int) (DIMENSION_CELL.getWidth() * 8), (int) (DIMENSION_CELL.getHeight() * 8));

    public Cell[][]  cells = new Cell[8][8];

    public ImageIcon activePieceImage;
    public Point     point;
    public boolean   isMouseDragging;

    public Board() {
        init();
    }

    public Cell getCell(Position position) {
        return this.cells[position.h][position.w];
    }

    private static Cell[][] createCellMatrix() {
        Cell[][] cells = new Cell[8][8];

        for (int h = 0; h < 8; h++) {
            for (int w = 0; w < 8; w++) {
                if (h % 2 == 0)
                    cells[h][w] = w % 2 == 0
                            ? new WhiteCell(new Position(h, w))
                            : new BlackCell(new Position(h, w));
                else
                    cells[h][w] = w % 2 == 1
                            ? new WhiteCell(new Position(h, w))
                            : new BlackCell(new Position(h, w));
            }
        }

        return cells;
    }

    private void init() {
        this.setPreferredSize(DIMENSION_WINDOW);
        this.setLayout(new GridLayout(8, 8));

        this.cells = createCellMatrix();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                this.add(this.cells[i][j]);
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
        ChessGUI.setQuality(g2d, 2);

        Font font = ChessGUI.Cell.NOTATION_FONT;

        g2d.setFont(font);

        for (int i = 0; i < 8; i++) {
            Position p = new Position(i, 0);
            Cell cell = this.getCell(p);
            int dx = cell.getLocation().x + 2;
            int dy = cell.getLocation().y + 25;

            String s = p.chessPosition.substring(1);

            g2d.setColor(cell instanceof WhiteCell ? ChessGUI.Cell.BLACK_COLOR : ChessGUI.Cell.WHITE_COLOR);
            Color shadowColor = cell instanceof WhiteCell
                    ? ChessGUI.Cell.WHITE_COLOR.darker().darker()
                    : ChessGUI.Cell.BLACK_COLOR.darker().darker();

            this.paintFontShadow(g2d, s, dx, dy, 1, shadowColor);
            g2d.drawString(s, dx, dy);
        }

        for (int i = 0; i < 8; i++) {
            Position p = new Position(7, i);
            Cell cell = this.getCell(p);
            int dx = cell.getLocation().x + 61;
            int dy = cell.getLocation().y + 75;

            String s = p.chessPosition.substring(0, 1);

            g2d.setColor(cell instanceof WhiteCell ? ChessGUI.Cell.BLACK_COLOR : ChessGUI.Cell.WHITE_COLOR);
            Color shadowColor = cell instanceof WhiteCell
                    ? ChessGUI.Cell.WHITE_COLOR.darker().darker()
                    : ChessGUI.Cell.BLACK_COLOR.darker().darker();

            this.paintFontShadow(g2d, s, dx, dy, 1, shadowColor);
            g2d.drawString(p.chessPosition.substring(0, 1), dx, dy);
        }
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        ChessGUI.setQuality(g2d, 2);

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
