package ru.chess.model.animator;

import ru.chess.gui.ImageReader;
import ru.chess.model.AbstractModel;
import ru.chess.model.Move;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.Timer;

public class LinearAnimator extends Animator {

    public final int steps;

    public LinearAnimator(AbstractModel model, Move move, int steps, Runnable action) {
        super(model, move, action);

        this.steps = steps;
    }

    @Override
    protected Void doInBackground() {
        animate();

        return null;
    }

    @Override
    protected void animate() {
        board.activePieceImage = ImageReader.get(move.moved());
        board.drawPiece        = true;
        board.point            = board.getCell(move.from()).getLocation();

        final Point point = new Point(
                board.point.x + board.activePieceImage.getIconWidth() / 2,
                board.point.y + board.activePieceImage.getIconHeight() / 2
        );

        Point endPoint = board.getCell(move.to()).getLocation();

        Point centerEndPoint = new Point(
                endPoint.x + board.activePieceImage.getIconWidth() / 2,
                endPoint.y + board.activePieceImage.getIconHeight() / 2
        );

        Timer timer = getTimer(point, centerEndPoint);

        timer.start();
    }

    private Timer getTimer(Point start, Point end) {
        final int[] fromX = {start.x};
        final int[] fromY = {start.y};

        int xDelta = (end.x - fromX[0]) / steps;
        int yDelta = (end.y - fromY[0]) / steps;

        final int[] dx = {fromX[0]};
        final int[] dy = {fromY[0]};

        final int[] step = {0};

        return new Timer(0, (event) -> {
            dx[0] = dx[0] + xDelta;
            dy[0] = dy[0] + yDelta;

            board.point = new Point(dx[0], dy[0]);

            step[0]++;

            if (step[0] == steps) {
                stop(event);
            }

            board.repaint();
        });
    }

    private void stop(ActionEvent event) {
        ((Timer) event.getSource()).stop();
        board.drawPiece = false;
        action.run();
    }
}
