package ru.chess.cell;

import ru.chess.*;
import ru.chess.gui.Board;
import ru.chess.gui.GUI;
import ru.chess.gui.ImageReader;
import ru.chess.position.Position;

import javax.swing.*;
import java.awt.*;

public abstract sealed class Cell extends JLabel permits WhiteCell, BlackCell {

    public enum State {
        DEFAULT,
        SELECTED,
        HIGHLIGHTED,
        CHECKMATE_NOTED,
        STALEMATE_NOTED
    }

    private static final Dimension DIMENSION = Board.DIMENSION_CELL;

    public PieceType         pieceType         = PieceType.NONE;
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
                ? GUI.Cell.WHITE_SELECTED_COLOR
                : GUI.Cell.BLACK_SELECTED_COLOR);

        this.setBorder(GUI.Cell.SELECTED_BORDER);

        this.repaint();
    }

    public void restore() {
        this.state = State.DEFAULT;

        this.setBackground(type == CellType.WHITE ? GUI.Cell.WHITE_COLOR : GUI.Cell.BLACK_COLOR);
        this.setBorder(BorderFactory.createLineBorder(this.getBackground(), 1));

        this.repaint();
    }

    public void highlight() {
        this.state = State.HIGHLIGHTED;

        this.setBackground(type == CellType.WHITE
                ? GUI.Cell.WHITE_MOVE_COLOR
                : GUI.Cell.BLACK_MOVE_COLOR);
        this.setBorder(BorderFactory.createLineBorder(this.getBackground(), 1));

        this.repaint();
    }

    public void noteLose() {
        this.state = State.CHECKMATE_NOTED;

        this.setBackground(GUI.Cell.CHECKMATE_NOTED);
        this.setBorder(BorderFactory.createLineBorder(this.getBackground(), 1));

        this.repaint();
    }

    public void noteDraw() {
        this.state = State.STALEMATE_NOTED;

        this.setBackground(GUI.Cell.STALEMATE_NOTED);
        this.setBorder(BorderFactory.createLineBorder(this.getBackground(), 1));

        this.repaint();
    }

    public void setPiece(PieceType pieceType) {
        ImageIcon icon = ImageReader.get(pieceType, DIMENSION.width, DIMENSION.height);

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

        GUI.setQuality(g2d, 2);

        super.paint(g2d);

        if (this.state == State.HIGHLIGHTED) {
            int d = GUI.Cell.AVAILABLE_MOVE_DIAMETER;

            int dx = this.getSize().height / 2 - d / 2;
            int dy = this.getSize().width / 2 - d / 2;

            g2d.setColor(GUI.Cell.AVAILABLE_MOVE_COLOR);

            g2d.drawOval(dx, dy, d, d);
            g2d.fillOval(dx, dy, d, d);
        }
    }
}
