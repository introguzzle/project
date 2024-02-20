package ru.chess;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Position {

    public String chessPosition;

    public int h;
    public int w;

    private static int toArrayWidth(String w) {
        return toArrayWidth(w.charAt(0));
    }

    private static int toArrayWidth(char w) {
        return switch(w) {
            case 'a' -> 0;
            case 'b' -> 1;
            case 'c' -> 2;
            case 'd' -> 3;
            case 'e' -> 4;
            case 'f' -> 5;
            case 'g' -> 6;
            case 'h' -> 7;

            default -> throw new IllegalStateException("Unexpected value: " + w);
        };
    }

    private static String toChessWidth(int x) {
        return switch(x) {
            case 0 -> "a";
            case 1 -> "b";
            case 2 -> "c";
            case 3 -> "d";
            case 4 -> "e";
            case 5 -> "f";
            case 6 -> "g";
            case 7 -> "h";

            default -> throw new IllegalStateException("Unexpected value: " + x);
        };
    }

    private void constructFromChessNotation() {
        this.h = 8 - Integer.parseInt(chessPosition.substring(1));
        this.w = toArrayWidth(chessPosition.charAt(0));
    }

    private void constructFromArrayNotation() {
        this.chessPosition = toChessWidth(this.w) + (8 - this.h);
    }

    public Position(String chessPosition) {
        this.chessPosition = chessPosition;

        constructFromChessNotation();
    }

    public Position(int h, int w) {
        this.h = h;
        this.w = w;

        constructFromArrayNotation();
    }

    public Position(Position position) {
        this.h = position.h;
        this.w = position.w;

        position.constructFromArrayNotation();

        this.chessPosition = position.chessPosition;
    }

    public int getChessHeight() {
        return Integer.parseInt(String.valueOf(this.chessPosition.charAt(1)));
    }

    public int getChessWidth() {
        return toArrayWidth(this.chessPosition.charAt(0)) + 1;
    }

    public Position up() {
        return new Position(h - 1, w);
    }

    public Position down() {
        return new Position(h + 1, w);
    }

    public Position left() {
        return new Position(h, w - 1);
    }

    public Position right() {
        return new Position(h, w + 1);
    }

    public List<Position> vertical(boolean isDirectedUp) {
        List<Position> positions = new ArrayList<>();

        String w = this.chessPosition.substring(0, 1);
        int    h = Integer.parseInt(this.chessPosition.substring(1));

        if (isDirectedUp) {
            for (int i = h + 1; i <= 8; i++) {
                positions.add(new Position(w + i));
            }
        } else {
            for (int i = h - 1; i > 0; i--) {
                positions.add(new Position(w + i));
            }
        }

        return positions;
    }

    public List<Position> horizontal(boolean isDirectedRight) {
        List<Position> positions = new ArrayList<>();

        String w = this.chessPosition.substring(0, 1);
        int    h = Integer.parseInt(this.chessPosition.substring(1));

        if (isDirectedRight) {
            for (int i = toArrayWidth(w) + 1; i < 8; i++)
                positions.add(new Position(toChessWidth(i) + h));
        } else {
            for (int i = toArrayWidth(w) - 1; i >= 0; i--)
                positions.add(new Position(toChessWidth(i) + h));
        }

        return positions;
    }

    public List<Position> diagonal(boolean isDirectedUp, boolean isDirectedRight) {
        List<Position> positions = new ArrayList<>();

        String w = this.chessPosition.substring(0, 1);

        if (isDirectedRight) {
            if (isDirectedUp) {

                for (int i = toArrayWidth(w) + 1,
                     h = Integer.parseInt(this.chessPosition.substring(1)) + 1;
                     i < 8 && h <= 8;
                     i++, h++)
                    positions.add(new Position(toChessWidth(i) + h));

            } else {

                for (int i = toArrayWidth(w) + 1,
                     h = Integer.parseInt(this.chessPosition.substring(1)) - 1;
                     i < 8 && h > 0;
                     i++, h--)
                    positions.add(new Position(toChessWidth(i) + h));

            }
        } else {
            if (isDirectedUp) {

                for (int i = toArrayWidth(w) - 1,
                     h = Integer.parseInt(this.chessPosition.substring(1)) + 1;
                     i >= 0 && h <= 8;
                     i--, h++)
                    positions.add(new Position(toChessWidth(i) + h));

            } else {

                for (int i = toArrayWidth(w) - 1,
                     h = Integer.parseInt(this.chessPosition.substring(1)) - 1;
                     i >= 0 && h > 0;
                     i--, h--)
                    positions.add(new Position(toChessWidth(i) + h));
            }
        }

        return positions;
    }

    public List<Position> knight() {
        List<Position> positions = new ArrayList<>();

        if (this.h > 1 && this.w < 7)
            positions.add(this.up().up().right());

        if (this.h < 6 && this.w < 7)
            positions.add(this.down().down().right());

        if (this.h > 1 && this.w > 0)
            positions.add(this.up().up().left());

        if (this.h < 6 && this.w > 0)
            positions.add(this.down().down().left());


        if (this.h > 0 && this.w < 6)
            positions.add(this.right().right().up());

        if (this.h < 7 && this.w < 6)
            positions.add(this.right().right().down());

        if (this.h > 0 && this.w > 1)
            positions.add(this.left().left().up());

        if (this.h < 7 && this.w > 1)
            positions.add(this.left().left().down());

        return positions;
    }

    public List<Position> around() {
        List<Position> positions = new ArrayList<>();

        if (this.h > 0)
            positions.add(this.up());

        if (this.h > 0 && this.w < 7)
            positions.add(this.up().right());

        if (this.w < 7)
            positions.add(this.right());

        if (this.h < 7 && this.w < 7)
            positions.add(this.down().right());


        if (this.h < 7)
            positions.add(this.down());

        if (this.h < 7 && this.w > 0)
            positions.add(this.down().left());

        if (this.w > 0)
            positions.add(this.left());

        if (this.h > 0 && this.w > 0)
            positions.add(this.up().left());

        return positions;
    }

    @Override
    public String toString() {
        return chessPosition + " = [" + h + ", " + w + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Position position = (Position) o;

        return h == position.h && w == position.w;
    }

    @Override
    public int hashCode() {
        return Objects.hash(h, w);
    }
}
