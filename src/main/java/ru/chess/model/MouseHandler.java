package ru.chess.model;

import ru.chess.PieceType;
import ru.chess.label.Cell;
import ru.chess.gui.Board;
import ru.chess.gui.ImageReader;
import ru.chess.position.Position;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseHandler extends MouseAdapter {

    public final Model model;
    public final Board board;

    public PieceType grabbedCellPieceType;
    public Position  grabbedCellPosition;

    public boolean successfulGrab;

    public MouseHandler(Model model) {
        this.model = model;
        this.board = model.getBoard();
    }

    public Cell getCell(MouseEvent e) {
        return (Cell) board.getComponentAt(e.getPoint());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Cell pressedCell = getCell(e);

        // Avoid static init
        if (model.initPawnPromotion) {
            PreStartConditions.promotePawns(model);
            return;
        }

        if (pressedCell.pieceType != PieceType.NONE && model.turn(pressedCell.absolutePieceType)) {
            this.successfulGrab = true;

            int width  = pressedCell.getWidth();
            int height = pressedCell.getHeight();

            board.setActivePieceImage(ImageReader.get(pressedCell.pieceType, width, height));

            this.grabbedCellPieceType = pressedCell.pieceType;
            this.grabbedCellPosition  = pressedCell.getPosition();

            model.showMoves(pressedCell);

            pressedCell.select();
            pressedCell.removePiece();

            this.mouseDragged(e);

            board.repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (successfulGrab) {
            this.successfulGrab = false;

            board.setDrawPiece(false);

            Cell chosenCell = getCell(e);

            if (chosenCell != null && chosenCell.state == Cell.State.HIGHLIGHTED) {
                model.lastMoveDestroyed = board.getCell(chosenCell.getPosition()).pieceType != PieceType.NONE;

                // Valid move, so we execute move
                model.setPiece(chosenCell.getPosition(), grabbedCellPieceType);
                board.repaint();

                SwingWorker<Void, Void> handler = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() {
                        model.handleMove(grabbedCellPosition, chosenCell.getPosition(), grabbedCellPieceType);
                        return null;
                    }
                };

                handler.execute();

            } else {
                // Invalid move, so we're getting back
                board.getCell(grabbedCellPosition).setPiece(grabbedCellPieceType);
            }

            model.restoreAll();

            board.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            board.repaint();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (successfulGrab) {
            // Look at paint() method in Board class
            board.setDrawingPoint(e.getPoint());
            board.setDrawPiece(true);

            board.setCursor(new Cursor(Cursor.HAND_CURSOR));
            board.repaint();
        }
    }
}
