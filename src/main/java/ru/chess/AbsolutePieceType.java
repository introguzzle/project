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
}
