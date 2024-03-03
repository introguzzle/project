package ru.chess.label;

import ru.chess.*;
import ru.chess.gui.Board;
import ru.chess.gui.GUI;
import ru.chess.gui.ImageReader;
import ru.chess.position.Position;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public abstract class Cell extends JLabel {

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

    private final Position       position;
    private final CellColorModel colorModel;
    private final Color          defaultColor;

    public Cell(Position position, CellColorModel colorModel) {
        this.position = position;
        this.colorModel = colorModel;

        this.defaultColor = colorModel.basic();

        init();
    }

    public void select() {
        this.state = State.SELECTED;

        this.setBackground(colorModel.selected());
        this.setBorder(BorderFactory.createLineBorder(colorModel.selectedBorder(), 4));

        this.repaint();
    }

    public void restore() {
        this.state = State.DEFAULT;

        this.setBackground(defaultColor);
        this.setBorder(BorderFactory.createLineBorder(this.getBackground(), 1));

        this.repaint();
    }

    public void highlight() {
        this.state = State.HIGHLIGHTED;

        this.setBackground(defaultColor.darker());
        this.setBorder(BorderFactory.createLineBorder(this.getBackground(), 1));

        this.repaint();
    }

    public void noteLose() {
        this.state = State.CHECKMATE_NOTED;

        this.setBackground(colorModel.checkmate());
        this.setBorder(BorderFactory.createLineBorder(this.getBackground(), 1));

        this.repaint();
    }

    public void noteDraw() {
        this.state = State.STALEMATE_NOTED;

        this.setBackground(colorModel.stalemate());
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
            int d = 25;

            int dx = this.getSize().height / 2 - d / 2;
            int dy = this.getSize().width / 2 - d / 2;

            g2d.setColor(colorModel.availableMoveCircle());

            g2d.drawOval(dx, dy, d, d);
            g2d.fillOval(dx, dy, d, d);
        }
    }

    public boolean isDefault() {
        return this.state == State.DEFAULT;
    }

    public boolean isSelected() {
        return this.state == State.SELECTED;
    }

    public CellColorModel getCellColorModel() {
        return this.colorModel;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "pieceType=" + pieceType +
                ", absolutePieceType=" + absolutePieceType +
                ", position=" + position +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cell cell = (Cell) o;
        return pieceType == cell.pieceType && Objects.equals(position, cell.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceType, position);
    }
}
