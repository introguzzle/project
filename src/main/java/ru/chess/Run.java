package ru.chess;

import ru.chess.position.Position;

public class Run {

    public static void main(String... ___) {
        Position.HORIZONTAL_BOUND = 8;
        Position.VERTICAL_BOUND = 8;

        Position a = new Position(0, 0);

        System.out.println(PieceType.of("wp"));
    }
}
