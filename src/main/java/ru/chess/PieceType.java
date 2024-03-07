package ru.chess;

import java.util.ArrayList;
import java.util.List;

public enum PieceType {
    NONE          ("", "", 0, false),

    WHITE_PAWN    ("wp", "P", 100,    false),
    WHITE_ROOK    ("wr", "R", 525,    false),
    WHITE_KNIGHT  ("wk", "N", 350,    false),
    WHITE_BISHOP  ("wb", "B", 350,    false),
    WHITE_QUEEN   ("wq", "Q", 900,    false),
    WHITE_CLOWN   ("wc", "C", 0,      true),
    WHITE_WIZARD  ("ww", "W", 0,      true),
    WHITE_TAMPLIER("wt", "T", 0,      true),
    WHITE_KING    ("wK", "K", 10000,  false),

    BLACK_PAWN    ("bp", "p", -100,   false),
    BLACK_ROOK    ("br", "r", -525,   false),
    BLACK_KNIGHT  ("bk", "n", -350,   false),
    BLACK_BISHOP  ("bb", "b", -350,   false),
    BLACK_QUEEN   ("bq", "q", -900,   false),
    BLACK_CLOWN   ("bc", "c", 0,      true),
    BLACK_WIZARD  ("bw", "w", 0,      true),
    BLACK_TAMPLIER("bt", "t", 0,      true),
    BLACK_KING    ("bK", "k", 10000,  false);

    public final String  code;
    public final String  fenCode;

    public final int     value;
    public final boolean extended;

    /**
     *
     * @param code Short code of this piece,
     *             first letter represents absolute type
     *             and second represents the class
     * @param value    Power of this piece
     * @param extended If it's not default piece
     */
    PieceType(String code, String fenCode, int value, boolean extended) {
        this.code = code;
        this.fenCode = fenCode;

        this.value = value;
        this.extended = extended;
    }

    public static PieceType of(String code) {
        for (PieceType pieceType: PieceType.values())
            if (code.equals(pieceType.code))
                return pieceType;

        return PieceType.NONE;
    }

    public AbsolutePieceType absolute() {
        if (isNone())
            return AbsolutePieceType.NONE;
        else
            return isWhite() ? AbsolutePieceType.WHITE : AbsolutePieceType.BLACK;
    }

    public PieceType invert() {
        if (isNone())
            return PieceType.NONE;

        String type = this.name().substring(6);

        return PieceType.valueOf(PieceType.class, (this.isWhite() ? "BLACK" : "WHITE") + "_" + type);
    }

    public static List<PieceType> allWhites() {
        List<PieceType> list = new ArrayList<>();

        for (PieceType pieceType: PieceType.values()) {
            if (pieceType.isWhite())
                list.add(pieceType);
        }

        return list;
    }

    public static List<PieceType> allBlacks() {
        List<PieceType> list = new ArrayList<>();

        for (PieceType pieceType: PieceType.values()) {
            if (pieceType.isBlack())
                list.add(pieceType);
        }

        return list;
    }

    public static PieceType of(String name, AbsolutePieceType type) {
        if (name.equals("NONE"))
            return NONE;

        return PieceType.valueOf(PieceType.class, (type.isWhite() ? "WHITE" : "BLACK") + "_" + name);
    }

    public boolean isNone() {
        return this == NONE;
    }

    public boolean isNotNone() {
        return this != NONE;
    }

    public boolean isBlack() {
        return this.name().startsWith("BLACK");
    }

    public boolean isWhite() {
        return this.name().startsWith("WHITE");
    }
}
