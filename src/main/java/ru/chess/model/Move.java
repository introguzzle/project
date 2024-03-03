package ru.chess.model;

import ru.chess.position.Position;

import java.util.HashMap;

public class Move extends HashMap<Position, Position> {

    private Position from = new Position("a1");
    private Position to   = new Position("a1");

    public Move() {
        super();
    }

    public Move(Position from, Position to) {
        super();
        super.put(from, to);

        this.from = from;
        this.to   = to;
    }

    public Position getTo() {
        return to;
    }

    public Position getFrom() {
        return from;
    }
}
