package ru.chess;

public enum AbsolutePieceType {
    NONE,
    WHITE,
    BLACK;

    public AbsolutePieceType invert() {
        if (this == NONE)
            return NONE;
        else
            return this == WHITE ? BLACK : WHITE;
    }

    public boolean isNotNone() {
        return this != NONE;
    }

    public boolean isNone() {
        return this == NONE;
    }

    public boolean isWhite() {
        return this == WHITE;
    }

    public boolean isBlack() {
        return this == BLACK;
    }
}
