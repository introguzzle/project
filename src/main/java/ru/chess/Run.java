package ru.chess;

import ru.chess.position.Position;

public class Run {

    public static void main(String... ___) {
        Position.HORIZONTAL_BOUND = 8;
        Position.VERTICAL_BOUND = 9;

        Position a = new Position("a1");

        System.out.println(a.around(null));
        System.out.println(a.horizontal(null, true));
        System.out.println(a.around(null, 2));
        System.out.println(false == false);
    }
}
