package ru.chess.position;

import java.util.Objects;

public abstract class AbstractPosition {

    public static int VERTICAL_BOUND;
    public static int HORIZONTAL_BOUND;

    protected final String chessPosition;

    protected final int h;
    protected final int w;

    protected static int toArrayWidth(String w) {
        return toArrayWidth(w.charAt(0));
    }

    protected static int toArrayWidth(char w) {
        return ((int) w) - 97;
    }

    protected static String toChessWidth(int w) {
        return String.valueOf((char)((char) 97 + w));
    }

    public AbstractPosition(String chessPosition) {
        this.chessPosition = chessPosition;

        this.h = VERTICAL_BOUND - Integer.parseInt(chessPosition.substring(1));
        this.w = toArrayWidth(chessPosition.charAt(0));
    }

    public AbstractPosition(int h, int w) {
        this.h = h;
        this.w = w;

        this.chessPosition = toChessWidth(this.w) + (VERTICAL_BOUND - this.h);
    }

    public AbstractPosition(AbstractPosition position) {
        this.h = position.h;
        this.w = position.w;

        this.chessPosition = toChessWidth(position.w) + (VERTICAL_BOUND - position.h);
    }

    public int getChessHeight() {
        return Integer.parseInt(this.chessPosition.substring(1));
    }

    public int getChessWidth() {
        return toArrayWidth(this.chessPosition.charAt(0)) + 1;
    }

    public abstract AbstractPosition up();
    public abstract AbstractPosition down();
    public abstract AbstractPosition right();
    public abstract AbstractPosition left();


    @Override
    public String toString() {
        return this.chessPosition + " = [" + this.h + ", " + this.w + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        AbstractPosition position = (AbstractPosition) o;

        return this.h == position.h && this.w == position.w;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.h, this.w);
    }

    public int getHeight() {
        return this.h;
    }

    public int getWidth() {
        return this.w;
    }

    public String getChessPosition() {
        return this.chessPosition;
    }
}
