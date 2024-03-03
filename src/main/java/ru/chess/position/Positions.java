package ru.chess.position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class Positions extends ArrayList<Position> {

    @SafeVarargs
    public Positions(List<Position>... lists) {
        for (List<Position> list: lists)
            this.addAll(list);
    }

    @Override
    public boolean add(Position position) {
        if (position.isValid())
            return super.add(position);
        else
            return false;
    }

    public void add(Predicate<? super Position> filter, Position position) {
        if (position.isValid())
            if (!filter.test(position))
                this.add(position);
    }

    public void add(Predicate<? super Position> filter, Position... positions) {
        for (Position p: positions) {
            this.add(filter, p);
        }
    }

    public void add(Position... positions) {
        super.addAll(Arrays.asList(positions));
    }
}
