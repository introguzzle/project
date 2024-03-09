package ru.chess.model.animator;

import ru.chess.gui.ImageReader;
import ru.chess.model.AbstractModel;
import ru.chess.model.Move;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;
import javax.swing.Timer;

public class AcceleratingAnimator extends Animator {

    public AcceleratingAnimator(AbstractModel model, Move move, Runnable action) {
        super(model, move, action);
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
        final double[] progress = {0};

        final double[] distance = {start.distance(end)};

        final double[] fromX = {start.x};
        final double[] fromY = {start.y};

        final int steps = Math.max((int) distance[0] / 25, 30) + random(-5, 5);

        final double[] xDelta = {(end.x - fromX[0]) / (double) steps};
        final double[] yDelta = {(end.y - fromY[0]) / (double) steps};

        final double[] dx = {fromX[0]};
        final double[] dy = {fromY[0]};

        return new Timer(0, (event) -> {
            progress[0] += 1.0 / (double) steps;

            double multiplier;

            if (board.point.distance(end) > 30) {
                multiplier = easeInOut(progress[0]);

            } else {
                multiplier = 0.5;
            }

            dx[0] += xDelta[0] * multiplier;
            dy[0] += yDelta[0] * multiplier;

            board.point = new Point((int) Math.round(dx[0]), (int) Math.round(dy[0]));

            distance[0] -= Math.sqrt(xDelta[0] * multiplier * multiplier * xDelta[0]
                    + yDelta[0] * multiplier * multiplier * yDelta[0]);

            board.repaint();

            if (distance[0] < 0.0) {
                stop(event);
            }
        });
    }

    private static int random(int left, int right) {
        return ThreadLocalRandom.current().nextInt(left, right);
    }

    private double easeInOut(double t) {
        if (t <= 0.5)
            return 2.0 * t * t;

        t -= 0.5;

        return 2.0 * t * (1.0 - t) + 0.5;
    }
}
