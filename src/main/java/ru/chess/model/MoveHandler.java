package ru.chess.model;

import ru.chess.AbsolutePieceType;
import ru.chess.Chess;
import ru.chess.PieceType;
import ru.chess.label.Cell;
import ru.chess.dialog.MateDialog;
import ru.chess.dialog.PawnPromotionDialog;
import ru.chess.position.Position;
import ru.chess.model.Model.State;

import javax.swing.*;

public final class MoveHandler {

    public static final int VERTICAL_BOUND;
    public static final int HORIZONTAL_BOUND;

    static {
        VERTICAL_BOUND   = Model.VERTICAL_BOUND;
        HORIZONTAL_BOUND = Model.HORIZONTAL_BOUND;
    }

    private MoveHandler() {

    }

    public static void executeCastling(Model model,
                                       Position newKingPosition,
                                       AbsolutePieceType kingType) {
        if (kingType.isWhite()) {
            if (newKingPosition.equals(model.whiteKingPosition.right().right())) {

                model.removePiece(model.whiteRightRookPosition);
                model.setPiece(newKingPosition.left(), PieceType.WHITE_ROOK);
                model.needWhiteCastlingNotify = false;
            }

            if (newKingPosition.equals(model.whiteKingPosition.left().left())) {

                model.removePiece(model.whiteLeftRookPosition);
                model.setPiece(newKingPosition.right(), PieceType.WHITE_ROOK);
                model.needWhiteCastlingNotify = false;
            }
        } else if (kingType.isBlack()) {

            if (newKingPosition.equals(model.blackKingPosition.right().right())) {

                model.removePiece(model.blackRightRookPosition);
                model.setPiece(newKingPosition.left(), PieceType.BLACK_ROOK);
                model.needBlackCastlingNotify = false;
            }

            if (newKingPosition.equals(model.blackKingPosition.left().left())) {

                model.removePiece(model.blackLeftRookPosition);
                model.setPiece(newKingPosition.right(), PieceType.BLACK_ROOK);
                model.needBlackCastlingNotify = false;
            }
        }
    }

    public static void executeEnPassant(Model model,
                                        Position oldPosition,
                                        Position newPosition,
                                        PieceType pawnType) {

        if (pawnType == PieceType.WHITE_PAWN)
            if (!model.lastMoveDestroyed && oldPosition.getWidth() != newPosition.getWidth()) {
                model.removePiece(model.lastBlackPawnMove.getTo());
            }

        if (pawnType == PieceType.BLACK_PAWN)
            if (!model.lastMoveDestroyed && oldPosition.getWidth() != newPosition.getWidth())
                model.removePiece(model.lastWhitePawnMove.getTo());

    }

    public static void executePawnPromotion(Model model,
                                            Position pawnPosition,
                                            AbsolutePieceType pawnType) {

        Chess owner = (Chess) SwingUtilities.getWindowAncestor(model.getBoard());

        PawnPromotionDialog pawnPromotionDialog = new PawnPromotionDialog(owner, pawnType);
        pawnPromotionDialog.setVisible(true);

        PieceType chosenPieceType = pawnPromotionDialog.pawnPromotionPanel.chosenPieceType;

        if (chosenPieceType != null)
            model.setPiece(pawnPosition, chosenPieceType);
        else
            executePawnPromotion(model, pawnPosition, pawnType);
    }

    public static void handleState(Model model, AbsolutePieceType absolutePieceType) {
        class Checker {
            static boolean isEmptyForAll(Model model, AbsolutePieceType absolutePieceType) {
                for (int i = 0; i < VERTICAL_BOUND; i++)
                    for (int j = 0; j < HORIZONTAL_BOUND; j++) {

                        if (model.getBoard().cells[i][j].absolutePieceType == absolutePieceType)
                            if (!ValidMoves.get(model, model.getBoard().cells[i][j]).isEmpty())
                                return false;
                    }

                return true;
            }

            static boolean isKingUnderAttack(Model model, AbsolutePieceType absoluteKingType) {
                PieceType         kingType       = absoluteKingType.isWhite() ? PieceType.WHITE_KING : PieceType.BLACK_KING;
                AbsolutePieceType enemyPieceType = kingType.absolute().invert();

                for (Position p: ValidMoves.acquireAllMoves(model.getBoard().cells, enemyPieceType)) {
                    if (model.getBoard().getCell(p).pieceType == kingType) {
                        return true;
                    }
                }

                return false;
            }
        }

        if (Checker.isEmptyForAll(model, absolutePieceType)) {
            if (Checker.isKingUnderAttack(model, absolutePieceType)) {
                model.state = absolutePieceType.isWhite()
                        ? State.CHECKMATE_TO_WHITE
                        : State.CHECKMATE_TO_BLACK;
            } else {
                model.state = State.STALEMATE;
            }
        }
    }

    public static void notifyEnPassant(Model     model,
                                       Position  oldPosition,
                                       Position  newPosition,
                                       PieceType movedPieceType) {
        if (movedPieceType == PieceType.WHITE_PAWN)
            model.lastWhitePawnMove = new Move(oldPosition, newPosition);

        if (movedPieceType == PieceType.BLACK_PAWN)
            model.lastBlackPawnMove = new Move(oldPosition, newPosition);
    }

    public static void notifyLastMoved(Model model, PieceType movedPieceType) {
        if (movedPieceType.absolute() == AbsolutePieceType.WHITE)
            if (movedPieceType != PieceType.WHITE_CLOWN && movedPieceType.isNotNone())
                model.lastWhiteMovedPieceType = movedPieceType;

        if (movedPieceType.absolute() == AbsolutePieceType.BLACK)
            if (movedPieceType != PieceType.BLACK_CLOWN && movedPieceType.isNotNone())
                model.lastBlackMovedPieceType = movedPieceType;
    }

    public static void callMateDialog(Model model, PieceType movedPieceType) {
        Chess owner = (Chess) SwingUtilities.getWindowAncestor(model.getBoard());

        highlightLoseCause(model, movedPieceType, model.state);

        model.getBoard().revalidate();
        model.getBoard().repaint();

        MateDialog mateDialog = new MateDialog(owner, model.state);
        mateDialog.setVisible(true);
    }

    private static void highlightLoseCause(Model model, PieceType pieceType, State state) {
        Cell[][] cells = model.getBoard().cells;

        if (state == State.CHECKMATE_TO_WHITE || state == State.CHECKMATE_TO_BLACK) {
            var       winnerType       = pieceType.absolute();
            PieceType lostKing         = winnerType.isWhite() ? PieceType.BLACK_KING : PieceType.WHITE_KING;

            Position  lostKingPosition = null;

            for (int i = 0; i < VERTICAL_BOUND; i++)
                for (int j = 0; j < HORIZONTAL_BOUND; j++) {
                    if (cells[i][j].pieceType == lostKing) {
                        cells[i][j].noteLose();
                        lostKingPosition = cells[i][j].getPosition();
                    }
                }

            for (Cell c : PseudoValidMoves.getIntersecting(cells, lostKingPosition, lostKing.absolute()))
                c.noteLose();
        }

        if (state == Model.State.STALEMATE) {
            for (int i = 0; i < VERTICAL_BOUND; i++)
                for (int j = 0; j < HORIZONTAL_BOUND; j++) {
                    if (cells[i][j].pieceType == PieceType.WHITE_KING)
                        cells[i][j].noteDraw();

                    if (cells[i][j].pieceType == PieceType.BLACK_KING)
                        cells[i][j].noteDraw();
                }
        }
    }

    public static void notifyCastling(Model model, Position position) {
        if (position.equals(model.whiteKingPosition)) {
            model.whiteKingMoved = true;
            model.needWhiteCastlingNotify = false;
            return;
        }

        if (position.equals(model.blackKingPosition)) {
            model.blackKingMoved = true;
            model.needBlackCastlingNotify = false;
            return;
        }

        if (position.equals(model.whiteLeftRookPosition)) {
            model.whiteLeftRookMoved = true;
            return;
        }

        if (position.equals(model.whiteRightRookPosition)) {
            model.whiteRightRookMoved = true;
            return;
        }

        if (position.equals(model.blackLeftRookPosition)) {
            model.blackLeftRookMoved = true;
            return;
        }

        if (position.equals(model.blackRightRookPosition)) {
            model.blackRightRookMoved = true;
        }
    }
}
