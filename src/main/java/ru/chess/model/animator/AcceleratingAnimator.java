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
        this.board.repaint();

        final double[] progress = {0};

        final double[] startDistance = {start.distance(end)};
        final double[] distance = {startDistance[0]};

        final double[] fromX = {start.x};
        final double[] fromY = {start.y};

        final int steps = 35;

        final double[] xDelta = {(end.x - fromX[0]) / (double) steps};
        final double[] yDelta = {(end.y - fromY[0]) / (double) steps};

        final double[] dx = {fromX[0]};
        final double[] dy = {fromY[0]};

        return new Timer(0, (event) -> {
            progress[0] += 1.0 / (double) steps;
            // Will not stop at 1, that's why its max is approx 1.6 or 1.7,
            // since max distance is basically movement from a1 to h8

            double multiplier = getMultiplier(startDistance[0], progress[0]);

            dx[0] += xDelta[0] * multiplier;
            dy[0] += yDelta[0] * multiplier;

            board.setDrawingPoint(new Point((int) dx[0], (int) dy[0]));
            board.repaint();

            distance[0] -= Math.sqrt(xDelta[0] * multiplier * multiplier * xDelta[0]
                    + yDelta[0] * multiplier * multiplier * yDelta[0]);

            if (distance[0] < 0.0) {
                stop(event);
            }
        });
    }

    private double getMultiplier(double startDistance, double progress) {
        double multiplier;

        if (startDistance > 480) {

            multiplier = easeInOut(progress, 1.75, 1.0);
        } else if (startDistance > 240) {

            multiplier = easeInOut(progress, 2.0, 1.25);
        } else {

            multiplier = easeInOut(progress, 3.0, 2.0);
        }

        return multiplier;
    }

    private double easeInOut(double t,
                             double beforeAcceleration,
                             double afterAcceleration) {
        // I believe t is changing between 0 and MAX ( calculated empirically )
        // upd: It can be calculated with this formula:

        // x_delta = x_delta * multiplier
        // y_delta = y_delta * multiplier
        // MAX_T = overall_distance / sqrt(x_delta ^ 2 + y_delta ^ 2)

        double MINIMAL_RESULT = 0.1;
        double SLOWDOWN = 0.7;

        double DOOM_POINT = 0.3; // After this value, animation slows
        double MAX = 1.7; // Max t

        if (t <= DOOM_POINT)
            return beforeAcceleration * t * t;

        t = MAX - t;

        // Calling MAX function to prevent negative speed

        return Math.max(afterAcceleration * t * (MAX - t) * SLOWDOWN + DOOM_POINT, MINIMAL_RESULT);
    }
}
