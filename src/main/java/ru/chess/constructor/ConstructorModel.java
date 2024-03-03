package ru.chess.constructor;

import ru.chess.PieceType;
import ru.chess.label.BlackCell;
import ru.chess.label.Cell;
import ru.chess.gui.Board;
import ru.chess.gui.ImageReader;
import ru.chess.label.ChoiceCell;
import ru.chess.label.WhiteCell;
import ru.chess.model.AbstractModel;
import ru.chess.model.Presets;
import ru.chess.model.SoundPlayer;
import ru.chess.position.Position;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ConstructorModel extends AbstractModel {

    public final int vertical;
    public final int horizontal;

    public ConstructorChoicePanel panel;
    public JTextField             textField;

    public ConstructorModel(int vertical, int horizontal) {
        super(vertical, horizontal);

        this.vertical   = vertical;
        this.horizontal = horizontal;

        this.panel = new ConstructorChoicePanel(vertical);
        this.board.add(this.panel);

        MouseHandler mouseHandler = new MouseHandler(this, board);

        this.panel.addMouseListener(mouseHandler);
        this.panel.addMouseMotionListener(mouseHandler);

        this.board.addMouseListener(mouseHandler);
        this.board.addMouseMotionListener(mouseHandler);

        this.textField = new JTextField();
        this.textField.setFont(new Font("Arial", Font.PLAIN, 20));
        this.textField.setEditable(false);
        this.textField.setCaretColor(this.textField.getBackground());
        this.textField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    public void highlightSet(Cell cell) {
        cell.setBackground(cell.getBackground().brighter());

        Timer timer = new Timer(50, (e) -> {
            ((Timer) e.getSource()).stop();
            cell.restore();
            board.repaint();
        });

        timer.start();

        SoundPlayer.playMoveSound();
    }

    public void highlightRemove(Cell cell) {
        cell.noteLose();

        Timer timer = new Timer(50, (e) -> {
            ((Timer) e.getSource()).stop();
            cell.restore();
            board.repaint();
        });

        timer.start();

        SoundPlayer.playMoveSound();
    }

    public ConstructorChoicePanel getPanel() {
        return this.panel;
    }

    public JTextField getTextField() {
        return textField;
    }

    @Override
    public void loadPreset(String preset) {
        Presets.Loader.load(this, preset);
    }

    private static final class MouseHandler extends MouseAdapter {

        public final ConstructorModel model;

        public final Board board;
        public final ConstructorChoicePanel panel;

        public PieceType grabbedCellPieceType;
        public Position  grabbedCellPosition;

        public final int shift;

        public boolean successfulGrab;

        private MouseHandler(ConstructorModel model, Board board) {
            this.model = model;
            this.board = board;
            this.panel = model.getPanel();
            this.shift = board.getPreferredSize().width;
        }

        public boolean isMouseEventOnBoard(MouseEvent e) {
            // e.getYOnScreen() < board.getLocationOnScreen().y + board.getHeight() &&
            return e.getXOnScreen() < board.getLocationOnScreen().x + board.getWidth();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (panel.contains(e.getPoint()) && !isMouseEventOnBoard(e)) {
                Cell pressedCell;

                if (panel.getComponentAt(e.getPoint()) instanceof ChoiceCell)
                    pressedCell = (Cell) panel.getComponentAt(e.getPoint());
                else
                    return;

                if (pressedCell.pieceType != PieceType.NONE) {
                    this.successfulGrab = true;

                    int width  = pressedCell.getWidth();
                    int height = pressedCell.getHeight();

                    board.activePieceImage = ImageReader.get(pressedCell.pieceType, width, height);
                    panel.activePieceImage = ImageReader.get(pressedCell.pieceType, width, height);

                    this.grabbedCellPieceType = pressedCell.pieceType;
                    this.grabbedCellPosition  = pressedCell.getPosition();

                    this.mouseDragged(e);

                    board.repaint();
                    panel.repaint();
                }
            }

            if (SwingUtilities.isRightMouseButton(e) && isMouseEventOnBoard(e)) {
                Cell pressedCell = (Cell) board.getComponentAt(e.getPoint());

                if (pressedCell.pieceType != PieceType.NONE) {
                    model.highlightRemove(pressedCell);
                    pressedCell.removePiece();
                    model.textField.setText(Presets.Reader.read(model));
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                if (successfulGrab) {
                    this.successfulGrab = false;

                    board.drawPiece = false;
                    panel.drawPiece = false;

                    Cell chosenCell = (Cell) board.getComponentAt(new Point(e.getX() + shift, e.getY()));

                    if (chosenCell != null) {
                        model.highlightSet(chosenCell);
                        board.getCell(chosenCell.getPosition()).setPiece(grabbedCellPieceType);
                        board.repaint();
                        panel.repaint();

                        model.textField.setText(Presets.Reader.read(model));
                    }

                    board.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    board.repaint();

                    panel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    panel.repaint();
                }
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                if (successfulGrab) {
                    board.point = new Point(e.getX() + shift, e.getY());
                    board.drawPiece = true;

                    panel.point = e.getPoint();
                    panel.drawPiece = true;

                    board.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    board.repaint();

                    panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    panel.repaint();
                }
            }
        }
    }
}
