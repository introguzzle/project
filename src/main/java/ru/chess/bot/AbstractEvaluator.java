package ru.chess.bot;

import ru.chess.model.Model;

public abstract class AbstractEvaluator implements Evaluator {

    public final Model model;

    public AbstractEvaluator(Model model) {
        this.model = model;
    }

    @Override
    public abstract int evaluate();
}
