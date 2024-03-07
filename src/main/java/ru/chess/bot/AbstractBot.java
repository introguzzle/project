package ru.chess.bot;

import ru.chess.model.Model;
import ru.chess.model.Move;

public abstract class AbstractBot implements Bot {

    public final Model model;

    /**
     * Board value evaluator
     */
    public Evaluator evaluator;

    public AbstractBot(Model model) {
        this.model = model;
    }

    @Override
    public abstract Move get();
}
