package ru.chess;

import java.util.ArrayList;
import java.util.List;

public enum PieceType {
    NONE          ("", false),

    WHITE_PAWN    ("wp", false),
    WHITE_ROOK    ("wr", false),
    WHITE_KNIGHT  ("wk", false),
    WHITE_BISHOP  ("wb", false),
    WHITE_QUEEN   ("wq", false),
    WHITE_CLOWN   ("wc", true),
    WHITE_WIZARD  ("ww", true),
    WHITE_TAMPLIER("wt", true),
    WHITE_KING    ("wK", false),

    BLACK_PAWN    ("bp", false),
    BLACK_ROOK    ("br", false),
    BLACK_KNIGHT  ("bk", false),
    BLACK_BISHOP  ("bb", false),
    BLACK_QUEEN   ("bq", false),
    BLACK_CLOWN   ("bc", true),
    BLACK_WIZARD  ("bw", true),
    BLACK_TAMPLIER("bt", true),
    BLACK_KING    ("bK", false);

    public final String  code;
    public final boolean extended;

    /**
     *
     * @param code Short code of this piece,
     *             first letter represents absolute type
     *             and second represents the class
     * @param extended If it's not default piece
     */
    PieceType(String code, boolean extended) {
        this.code = code;
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
