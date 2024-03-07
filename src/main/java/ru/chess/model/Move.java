package ru.chess.model;

import ru.chess.PieceType;
import ru.chess.position.Position;

public record Move(Position from, Position to, PieceType moved) {

}
