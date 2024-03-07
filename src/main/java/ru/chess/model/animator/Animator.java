package ru.chess.model.animator;

import ru.chess.gui.Board;
import ru.chess.model.AbstractModel;
import ru.chess.model.Move;

import javax.swing.*;

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

    @Override
    protected abstract Void doInBackground();

    protected abstract void animate();
}
