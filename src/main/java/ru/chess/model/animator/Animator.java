package ru.chess.model.animator;

import ru.chess.gui.Board;
import ru.chess.gui.ImageReader;
import ru.chess.model.AbstractModel;
import ru.chess.model.Move;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;


/**
 * Class that animates move on board
 */
public abstract class Animator extends SwingWorker<Void, Void> {

    public final AbstractModel model;
    public final Move move;
    public final Runnable action;

    public final Board board;

    public final Point start;
    public final Point end;

    public static final int CELL_WIDTH = Board.DIMENSION_CELL.width;
    public static final int CELL_HEIGHT = Board.DIMENSION_CELL.height;

    public Animator(AbstractModel model, Move move, Runnable action) {
        this.model  = model;
        this.move   = move;
        this.action = action;

        this.board  = model.getBoard();

        this.start  = getStart();
        this.end    = getEnd();
    }

    // Need to ensure board.activePieceImage is not null and set to
    // some icon

    private Point getStart() {
        Point start = board.getCell(move.from()).getLocation();

        return new Point(
                start.x + board.getActivePieceImage().getIconWidth() / 2,
                start.y + board.getActivePieceImage().getIconHeight() / 2
        );
    }

    private Point getEnd() {
        Point end = board.getCell(move.to()).getLocation();

        return new Point(
                end.x + board.getActivePieceImage().getIconWidth() / 2,
                end.y + board.getActivePieceImage().getIconHeight() / 2
        );
    }

    protected void stop(ActionEvent event) {
        ((Timer) event.getSource()).stop();
        board.setDrawPiece(false);
        board.repaint();
        model.setPiece(move.to(), move.type());

        if (action != null)
            action.run();

        model.botMoveOngoing = false;
    }

    @Override
    protected Void doInBackground() {
        board.setActivePieceImage(ImageReader.get(move.type()));
        board.setDrawPiece(true);

        board.setDrawingPoint(getStart());

        Timer timer = timer();

        timer.start();

        return null;
    }

    /**
     * Implementation of this method in subclasses controls animation
     * @return Animating timer
     */
    protected abstract Timer timer();
}
