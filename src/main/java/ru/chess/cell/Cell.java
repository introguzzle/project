package ru.chess.cell;

import ru.chess.*;

import javax.swing.*;
import java.awt.*;

public abstract sealed class Cell extends JLabel permits WhiteCell, BlackCell {

    public enum State {
        DEFAULT,
        SELECTED,
        HIGHLIGHTED,
        NOTED
    }

    private static final Dimension DIMENSION = Board.DIMENSION_CELL;

    public PieceType pieceType         = PieceType.NONE;
    public AbsolutePieceType absolutePieceType = AbsolutePieceType.NONE;

    public State state = State.DEFAULT;

    public final CellType type;
    public final Position position;

    public Cell(Position position, CellType type) {
        this.position = position;
        this.type = type;

        init();
    }

    public void select() {
        this.state = State.SELECTED;

        this.setBackground(type == CellType.WHITE
                ? ChessGUI.Cell.WHITE_SELECTED_COLOR
                : ChessGUI.Cell.BLACK_SELECTED_COLOR);

        this.setBorder(ChessGUI.Cell.SELECTED_BORDER);

        this.repaint();
    }

    public void restore() {
        this.state = State.DEFAULT;

        this.setBackground(type == CellType.WHITE ? ChessGUI.Cell.WHITE_COLOR : ChessGUI.Cell.BLACK_COLOR);
        this.setBorder(BorderFactory.createLineBorder(this.getBackground(), 1));

        this.repaint();
    }

    public void highlight() {
        this.state = State.HIGHLIGHTED;

        this.setBackground(type == CellType.WHITE
                ? ChessGUI.Cell.WHITE_MOVE_COLOR
                : ChessGUI.Cell.BLACK_MOVE_COLOR);
        this.setBorder(BorderFactory.createLineBorder(this.getBackground(), 1));

        this.repaint();
    }

    public void note() {
        this.state = State.NOTED;

        this.setBackground(ChessGUI.Cell.NOTED);
        this.setBorder(BorderFactory.createLineBorder(this.getBackground(), 1));

        this.repaint();
    }

    public void setPiece(PieceType pieceType) {
        ImageIcon icon = ImageReader.get(pieceType);

        super.setIcon(icon);
        this.pieceType = pieceType;
        this.absolutePieceType = pieceType.absolute();
    }

    public void removePiece() {
        super.setIcon(null);
        this.pieceType = PieceType.NONE;
        this.absolutePieceType = pieceType.absolute();
    }

    public Position getPosition() {
        return this.position;
    }

    private void init() {
        this.setPreferredSize(DIMENSION);
        this.setOpaque(true);
        this.setHorizontalAlignment(CENTER);
        this.setBorder(BorderFactory.createLineBorder(this.getBackground(), 1));

        this.setHorizontalTextPosition(LEFT);
        this.setVerticalTextPosition(BOTTOM);

        this.restore();
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        ChessGUI.setQuality(g2d, 2);

        super.paint(g2d);

        if (this.state == State.HIGHLIGHTED) {
            int r = ChessGUI.Cell.AVAILABLE_MOVE_RADIUS;

            int dx = this.getSize().height / 2 - r / 2;
            int dy = this.getSize().width / 2 - r / 2;

            g2d.setColor(ChessGUI.Cell.AVAILABLE_MOVE_COLOR);

            g2d.drawOval(dx, dy, r, r);
            g2d.fillOval(dx, dy, r, r);
        }
    }
}
