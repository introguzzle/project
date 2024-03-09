package ru.chess.model.animator;

import ru.chess.gui.Board;
import ru.chess.model.AbstractModel;
import ru.chess.model.Move;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public abstract class Animator extends SwingWorker<Void, Void> {

    public final AbstractModel model;
    public final Move move;
    public final Runnable action;

    public final Board board;

    public Animator(AbstractModel model, Move move, Runnable action) {
        this.model = model;
        this.move = move;
        this.action = action;

        this.board = model.getBoard();
    }

    // Need to ensure board.activePieceImage is not null and set to
    // some icon

    protected Point getStart() {
        Point start = board.getCell(move.from()).getLocation();

        return new Point(
                start.x + board.activePieceImage.getIconWidth() / 2,
                start.y + board.activePieceImage.getIconHeight() / 2
        );
    }

    protected Point getEnd() {
        Point endPoint = board.getCell(move.to()).getLocation();

        return new Point(
                endPoint.x + board.activePieceImage.getIconWidth() / 2,
                endPoint.y + board.activePieceImage.getIconHeight() / 2
        );
    }

    protected void stop(ActionEvent event) {
        ((Timer) event.getSource()).stop();
        board.drawPiece = false;

        model.setPiece(move.to(), move.moved());
        action.run();
    }

    @Override
    protected abstract Void doInBackground();

    protected abstract void animate();
}
