package ru.chess.constructor;

import ru.chess.Fen;
import ru.chess.PieceType;
import ru.chess.label.Cell;
import ru.chess.gui.Board;
import ru.chess.gui.ImageReader;
import ru.chess.label.ChoiceCell;
import ru.chess.model.AbstractModel;
import ru.chess.model.Presets;
import ru.chess.model.SoundPlayer;
import ru.chess.position.Position;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ConstructorModel extends AbstractModel {

    public final int vertical;
    public final int horizontal;

    private final ConstructorChoicePanel panel;
    private final JTextField textField;
    private final JTextField fenTextField;

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
        this.textField.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 4));

        this.fenTextField = new JTextField();
        this.fenTextField.setFont(new Font("Arial", Font.PLAIN, 20));
        this.fenTextField.setEditable(false);
        this.fenTextField.setCaretColor(this.fenTextField.getBackground());
        this.fenTextField.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 4));
    }

    public void feedbackSet(Cell cell) {
        cell.brighten();
        SoundPlayer.playMoveSound();

        Timer timer = new Timer(50, (e) -> {
            ((Timer) e.getSource()).stop();
            cell.restore();
            board.repaint();
        });

        timer.start();
    }

    public void feedbackRemove(Cell cell) {
        cell.noteLose();
        SoundPlayer.playMoveSound();

        Timer timer = new Timer(50, (e) -> {
            ((Timer) e.getSource()).stop();
            cell.restore();
            board.repaint();
        });

        timer.start();
    }


    private void updateTextFields() {
        textField.setText(Presets.Reader.read(this));
        textField.setCaretPosition(0);

        fenTextField.setText(Fen.toFen(this, true));
        fenTextField.setCaretPosition(0);
    }

    public ConstructorChoicePanel getPanel() {
        return this.panel;
    }

    public JTextField getTextField() {
        return textField;
    }

    public JTextField getFenTextField() {
        return fenTextField;
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

        public boolean panelInvoked = true;
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
            if (SwingUtilities.isLeftMouseButton(e) && !isMouseEventOnBoard(e)) {
                Cell pressedCell;

                if (panel.getComponentAt(e.getPoint()) instanceof ChoiceCell)
                    pressedCell = (Cell) panel.getComponentAt(e.getPoint());
                else
                    return;

                if (pressedCell.pieceType != PieceType.NONE) {
                    this.successfulGrab = true;

                    int width  = pressedCell.getWidth();
                    int height = pressedCell.getHeight();

                    board.setActivePieceImage(ImageReader.get(pressedCell.pieceType, width, height));
                    panel.activePieceImage = ImageReader.get(pressedCell.pieceType, width, height);

                    this.grabbedCellPieceType = pressedCell.pieceType;
                    this.grabbedCellPosition  = pressedCell.getPosition();

                    this.panelInvoked = true;

                    board.repaint();
                    panel.repaint();

                    this.mouseDragged(e);
                }
            } else if (SwingUtilities.isLeftMouseButton(e)) {
                Cell pressedCell;

                if (board.getComponentAt(e.getPoint()) instanceof Cell)
                    pressedCell = (Cell) board.getComponentAt(e.getPoint());
                else
                    return;

                if (pressedCell.pieceType != PieceType.NONE) {
                    this.successfulGrab = true;

                    int width  = pressedCell.getWidth();
                    int height = pressedCell.getHeight();

                    board.setActivePieceImage(ImageReader.get(pressedCell.pieceType, width, height));
                    panel.activePieceImage = ImageReader.get(pressedCell.pieceType, width, height);

                    this.grabbedCellPieceType = pressedCell.pieceType;
                    this.grabbedCellPosition  = pressedCell.getPosition();

                    pressedCell.removePiece();

                    this.panelInvoked = false;

                    board.repaint();
                    panel.repaint();
                    this.mouseDragged(e);
                }
            }

            if (SwingUtilities.isRightMouseButton(e) && isMouseEventOnBoard(e)) {
                Cell pressedCell = (Cell) board.getComponentAt(e.getPoint());

                if (pressedCell.pieceType != PieceType.NONE) {
                    model.feedbackRemove(pressedCell);
                    pressedCell.removePiece();

                    model.updateTextFields();
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                if (successfulGrab) {
                    this.successfulGrab = false;

                    board.setDrawPiece(false);
                    panel.drawPiece = false;

                    Cell chosenCell;

                    if (panelInvoked)
                        chosenCell = (Cell) board.getComponentAt(new Point(e.getX() + shift, e.getY()));
                    else
                        chosenCell = (Cell) board.getComponentAt(e.getPoint());

                    if (chosenCell != null) {
                        model.feedbackSet(chosenCell);
                        board.getCell(chosenCell.getPosition()).setPiece(grabbedCellPieceType);
                        board.repaint();
                        panel.repaint();

                        model.updateTextFields();

                    } else if (!panelInvoked) {
                        model.feedbackSet(board.getCell(grabbedCellPosition));
                        board.getCell(grabbedCellPosition).setPiece(grabbedCellPieceType);
                        board.repaint();
                        panel.repaint();

                        model.updateTextFields();
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
            if (SwingUtilities.isLeftMouseButton(e) && successfulGrab) {
                if (panelInvoked) {
                    board.setDrawingPoint(new Point(e.getX() + shift, e.getY()));
                    board.setDrawPiece(true);

                    panel.point = e.getPoint();
                    panel.drawPiece = true;

                    board.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    board.repaint();

                    panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    panel.repaint();
                } else {
                    board.setDrawingPoint(e.getPoint());
                    board.setDrawPiece(true);
                    board.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    board.repaint();

                    panel.point = new Point(e.getX() - shift, e.getY());
                    panel.drawPiece = false;

                    panel.repaint();
                }
            }
        }
    }
}
