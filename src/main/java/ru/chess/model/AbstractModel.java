package ru.chess.model;

import ru.chess.PieceType;
import ru.chess.gui.ImageReader;
import ru.chess.label.Cell;
import ru.chess.gui.Board;
import ru.chess.model.animator.AcceleratingAnimator;
import ru.chess.model.animator.LinearAnimator;
import ru.chess.position.AbstractPosition;
import ru.chess.position.Position;

import javax.swing.*;
import java.awt.*;

public abstract class AbstractModel {

    protected Board board;

    public static int VERTICAL_BOUND;
    public static int HORIZONTAL_BOUND;

    public AbstractModel(int vertical, int horizontal) {
        VERTICAL_BOUND   = vertical;
        HORIZONTAL_BOUND = horizontal;

        // Stupid static hack. If this constructor isn't being called,
        // entire Position class doesn't work.
        // But on other hand, it does make sense,
        // because Position does need to know what are bounds of Board
        // which in its turn need Model to handle game

        AbstractPosition.VERTICAL_BOUND   = vertical;
        AbstractPosition.HORIZONTAL_BOUND = horizontal;

        this.board = new Board(vertical, horizontal);

        SoundPlayer.playStartSound();
    }

    public void removePiece(Position position) {
        if (position.isValid())
            board.getCell(position).removePiece();
    }

    public void restoreAll() {
        for (int i = 0; i < VERTICAL_BOUND; i++)
            for (int j = 0; j < HORIZONTAL_BOUND; j++)
                board.cells[i][j].restore();
    }

    public void setPiece(Position position, PieceType pieceType) {
        board.getCell(position).setPiece(pieceType);
    }

    private static final class NonLinearAnimator extends SwingWorker<Void, Void> {
        private final int steps;

        private final Move move;
        private final Board board;
        private final Runnable action;

        public NonLinearAnimator(AbstractModel model, Move move, int steps, Runnable action) {
            this.move = move;

            this.steps = steps;
            this.board = model.getBoard();
            this.action = action;
        }

        @Override
        protected Void doInBackground() {
            board.activePieceImage = ImageReader.get(move.moved());
            board.drawPiece = true;
            board.point  = board.getCell(move.from()).getLocation();

            final Point point0 = new Point(board.point.x + board.activePieceImage.getIconWidth() / 2,
                    board.point.y + board.activePieceImage.getIconHeight() / 2);

            Point endPoint0 = board.getCell(move.to()).getLocation();

            Point endPoint = new Point(
                    endPoint0.x + board.activePieceImage.getIconWidth() / 2,
                    endPoint0.y + board.activePieceImage.getIconHeight() / 2
            );

            final int[] fromX = {point0.x};
            final int[] fromY = {point0.y};

            int endX = endPoint.x;
            int endY = endPoint.y;

            final double acceleration = 50.0; // Ускорение
            final double initialVelocity = 999; // Начальная скорость
            final double timeStep = 30; // Время шага в миллисекундах

            double currentVelocity = initialVelocity;
            double currentTime = 0;

            while (currentVelocity >= 0) { // Пока скорость не станет отрицательной
                // Рассчитываем изменение позиции с учетом равноускоренного движения
                double displacement = (currentVelocity * currentTime) + (0.5 * acceleration * currentTime * currentTime);

                // Переводим смещение в целочисленные координаты
                int displacementX = (int) Math.round(displacement * (endX - fromX[0]) / Math.hypot(endX - fromX[0], endY - fromY[0]));
                int displacementY = (int) Math.round(displacement * (endY - fromY[0]) / Math.hypot(endX - fromX[0], endY - fromY[0]));

                // Обновляем координаты объекта
                int newX = fromX[0] + displacementX;
                int newY = fromY[0] + displacementY;
                board.point = new Point(newX, newY);

                // Перерисовываем доску
                board.repaint();

                // Обновляем текущую скорость
                currentVelocity += acceleration * (timeStep / 1000);

                try {
                    Thread.sleep((long) timeStep);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                // Обновляем текущее время
                currentTime += timeStep / 1000; // Преобразование миллисекунд в секунды
            }

            board.drawPiece = false;
            action.run();

            return null;
        }
    }

    public void movePiece(Move move, Runnable callback) {
        this.removePiece(move.from());

        new AcceleratingAnimator(this, move, callback).execute();
    }

    public Cell[][] copyCells() {
        Cell[][] result = Board.createCellMatrix();

        for (int i = 0; i < VERTICAL_BOUND; i++) {
            for (int j = 0; j < HORIZONTAL_BOUND; j++) {
                result[i][j].setPiece(board.cells[i][j].pieceType);
            }
        }

        return result;
    }

    public void reset() {
        for (int i = 0; i < VERTICAL_BOUND; i++)
            for (int j = 0; j < HORIZONTAL_BOUND; j++)
                board.cells[i][j].removePiece();

    }

    public abstract void loadPreset(String preset);

    public Board getBoard() {
        return this.board;
    }
}
