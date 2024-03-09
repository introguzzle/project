package ru.chess.model.animator;

import ru.chess.gui.ImageReader;
import ru.chess.model.AbstractModel;
import ru.chess.model.Move;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
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

        board.point = getStart();

        Timer timer = getTimer(getStart(), getEnd());

        timer.start();
    }

    private Timer getTimer(Point start, Point end) {
        int delay = 0;

        final int[] fromX = {start.x};
        final int[] fromY = {start.y};

        int xDelta = (end.x - fromX[0]) / steps;
        int yDelta = (end.y - fromY[0]) / steps;

        final int[] dx = {fromX[0]};
        final int[] dy = {fromY[0]};

        double[] distances = {0, 0};

        return new Timer(delay, (event) -> {
            dx[0] = dx[0] + xDelta;
            dy[0] = dy[0] + yDelta;

            distances[0] = board.point.distance(end);

            board.point = new Point(dx[0], dy[0]);

            distances[1] = board.point.distance(end);

            if (board.point.distance(end) <= Math.max(xDelta, yDelta)) {
                stop(event);
            }

            // In case distance inverts sign, e.g. jumped behind needed cell
            if (distances[1] > distances[0]) {
                stop(event);
            }

            board.repaint();
        });
    }
}
