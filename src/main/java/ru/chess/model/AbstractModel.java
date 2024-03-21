package ru.chess.model;

import ru.chess.PieceType;
import ru.chess.label.Cell;
import ru.chess.gui.Board;
import ru.chess.model.animator.AcceleratingAnimator;

import ru.chess.position.AbstractPosition;
import ru.chess.position.Position;

import javax.swing.*;
import java.awt.*;

public abstract class AbstractModel {

    public boolean botMoveOngoing;

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

    public void movePiece(Move move) {
        this.removePiece(move.from());
        this.board.getCell(move.to()).pieceType = move.type();

        new AcceleratingAnimator(this, move, null).execute();
    }

    public void movePiece(Move move, Runnable callback) {
        this.removePiece(move.from());
        this.board.getCell(move.to()).pieceType = move.type();

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
