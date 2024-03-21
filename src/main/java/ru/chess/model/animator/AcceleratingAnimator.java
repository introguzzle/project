package ru.chess.model.animator;

import ru.chess.model.AbstractModel;
import ru.chess.model.Move;

import java.awt.*;
import javax.swing.Timer;

public class AcceleratingAnimator extends Animator {

    public AcceleratingAnimator(AbstractModel model, Move move, Runnable action) {
        super(model, move, action);
    }

    @Override
    public Timer timer() {
        model.botMoveOngoing = true;

        this.board.repaint();

        double startDistance = start.distance(end);

        double[] distance = {startDistance};

        final int steps = 50;

        double[] xDelta = {(end.x - start.x) / (double) steps};
        double[] yDelta = {(end.y - start.y) / (double) steps};

        double[] dx = {start.x};
        double[] dy = {start.y};

        return new Timer(0, (event) -> {
            // Will not stop at 1, that's why its max is approx 1.6 or 1.7,
            // since max distance is basically movement from a1 to h8
            double multiplier = scale(distance[0], startDistance);

            dx[0] += xDelta[0] * multiplier;
            dy[0] += yDelta[0] * multiplier;

            double m = multiplier * multiplier;

            board.setDrawingPoint(new Point((int) dx[0], (int) dy[0]));
            board.repaint();

            distance[0] -= Math.sqrt(xDelta[0] * xDelta[0] * m + yDelta[0] * yDelta[0] * m);

            if (distance[0] <= 0.0) {
                stop(event);
            }
        });
    }

    private static double normalize(double currentDistance,
                                    double totalDistance) {
        return Math.min(1.0, (totalDistance - currentDistance) / totalDistance);
    }

    private static final double MAX_DISTANCE;

    static {
        double max   = Math.max(AbstractModel.HORIZONTAL_BOUND, AbstractModel.VERTICAL_BOUND);
        double hypot = Math.sqrt(CELL_HEIGHT * CELL_HEIGHT + CELL_WIDTH * CELL_WIDTH);

        MAX_DISTANCE = (max - 2) * hypot + hypot;
    }

    private static double scale(double currentDistance,
                                double totalDistance) {
        double f = totalDistance / MAX_DISTANCE;

        return inOut(currentDistance, totalDistance, 1.6 - f);
    }

    private static double inOut(double currentDistance,
                                double totalDistance,
                                double factor) {
        double t = (normalize(currentDistance, totalDistance) + 2.7) / 0.24;
        double f = Math.max(factor, 1.0);

        //return (Math.sin(t / 0.3) + Math.cos(t / 0.3) + factor) / 2.0;
        return Math.sin(t) + Math.cos(t) / (0.8 * f) + f;
    }
}
