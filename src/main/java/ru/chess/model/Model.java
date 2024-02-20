package ru.chess.model;

import ru.chess.*;
import ru.chess.cell.Cell;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Stack;

public class Model {

    public enum State {
        ONGOING,
        CHECK_TO_WHITE,
        CHECK_TO_BLACK,
        STALEMATE,
        CHECKMATE_TO_WHITE,
        CHECKMATE_TO_BLACK
    }

    private Board board;

    protected boolean initLoad = true;
    protected String  initPreset;

    protected Stack<String> history;

    protected State   state = State.ONGOING;
    protected boolean turn  = true;

    public boolean whiteKingMoved      = false;
    public boolean whiteLeftRookMoved  = false;
    public boolean whiteRightRookMoved = false;

    public boolean blackKingMoved      = false;
    public boolean blackLeftRookMoved  = false;
    public boolean blackRightRookMoved = false;

    public Model() {
        init();
    }

    private void init() {
        board   = new Board();
        history = new Stack<>();

        MouseHandler mouseHandler = new MouseHandler(this);

        board.addMouseListener(mouseHandler);
        board.addMouseMotionListener(mouseHandler);

        board.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "Left");
        board.getActionMap().put("Left", new LeftArrowKeyHandleAction("Left", this));
    }

    public void loadDefaultPreset() {
        loadPreset(PresetLoader.DEFAULT);
    }

    public void loadPreset(String preset) {
        if (initLoad) {
            PresetLoader.load(this, preset);

            history.push(PresetReader.read(this));

            initPreset = preset;
            initLoad   = false;

        } else {
            PresetLoader.load(this, preset);
        }
    }

    private void restoreAll() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board.cells[i][j].restore();
            }
        }
    }

    private void showMoves(Cell cell) {
        for (Position position: ValidMoves.get(this, cell)) {
            board.getCell(position).highlight();
        }
    }

    public void removePiece(Position position) {
        board.getCell(position).removePiece();
    }

    public void setPiece(Position position, PieceType pieceType) {
        board.getCell(position).setPiece(pieceType);
    }

    private void handleState(AbsolutePieceType absolutePieceType) {

        if (ValidMoves.isEmptyForAll(this, absolutePieceType)) {

            if (ValidMoves.isKingUnderAttack(this, absolutePieceType)) {
                state = absolutePieceType == AbsolutePieceType.WHITE
                        ? State.CHECKMATE_TO_WHITE
                        : State.CHECKMATE_TO_BLACK;
            } else {
                state = State.STALEMATE;
            }
        }
    }

    private void handleMove(Position  oldPosition,
                            Position  newPosition,
                            PieceType movedPieceType) {

        AbsolutePieceType absolutePieceType = movedPieceType.absolute();

        if (movedPieceType == PieceType.WHITE_PAWN || movedPieceType == PieceType.BLACK_PAWN) {
            if (newPosition.getChessHeight() == (movedPieceType.absolute() == AbsolutePieceType.WHITE ? 8 : 1))
                executePawnPromotion(newPosition, movedPieceType.absolute());
        }

        if (movedPieceType == PieceType.WHITE_KING || movedPieceType == PieceType.BLACK_KING)
            if (Math.abs(oldPosition.w - newPosition.w) == 2)
                executeCastling(newPosition, movedPieceType.absolute());

        handleState(absolutePieceType.invert());

        turn = !turn;

        history.push(PresetReader.read(this));

        notifyCastling(newPosition);

        if (state == State.CHECKMATE_TO_WHITE || state == State.CHECKMATE_TO_BLACK || state == State.STALEMATE) {
            Chess owner = (Chess) SwingUtilities.getWindowAncestor(board);

            highlightLoseCause(movedPieceType);

            board.repaint();

            MateDialog mateDialog = new MateDialog(owner, state);
            mateDialog.setVisible(true);
        }
    }

    private void undoMove() {
        if (history.size() > 1) {
            history.pop();
            loadPreset(history.peek());
        } else {
            loadPreset(history.firstElement());
        }
    }

    private void highlightLoseCause(PieceType pieceType) {

        AbsolutePieceType winnerType = pieceType.absolute();
        PieceType         lostKing   = winnerType == AbsolutePieceType.WHITE ? PieceType.BLACK_KING : PieceType.WHITE_KING;

        Position          lostKingPosition = null;

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                if (board.cells[i][j].pieceType == lostKing) {
                    board.cells[i][j].setBackground(Color.RED);
                    lostKingPosition = board.cells[i][j].position;
                }
            }

        for (Cell c: PseudoValidMoves.getIntersecting(board, lostKingPosition, lostKing.absolute()))
            c.setBackground(Color.RED);
    }

    private boolean turn(AbsolutePieceType absolutePieceType) {
        return turn == (absolutePieceType == AbsolutePieceType.WHITE);
    }

    private void executePawnPromotion(Position pawnPosition, AbsolutePieceType pawnType) {
        Chess owner = (Chess) SwingUtilities.getWindowAncestor(board);

        PawnPromotionDialog pawnPromotionDialog = new PawnPromotionDialog(owner, pawnType);
        pawnPromotionDialog.setVisible(true);

        setPiece(pawnPosition, pawnPromotionDialog.pawnPromotionPanel.chosenPieceType);
    }

    private void executeCastling(Position kingPosition, AbsolutePieceType kingType) {
        if (kingType == AbsolutePieceType.WHITE) {
            if (kingPosition.equals(new Position("g1"))) {
                removePiece(new Position("h1"));
                setPiece(new Position("f1"), PieceType.WHITE_ROOK);
            }

            if (kingPosition.equals(new Position("c1"))) {
                removePiece(new Position("a1"));
                setPiece(new Position("d1"), PieceType.WHITE_ROOK);
            }
        }

        else if (kingType == AbsolutePieceType.BLACK) {
            if (kingPosition.equals(new Position("g8"))) {
                removePiece(new Position("h8"));
                setPiece(new Position("f8"), PieceType.BLACK_ROOK);
            }

            if (kingPosition.equals(new Position("c8"))) {
                removePiece(new Position("a8"));
                setPiece(new Position("d8"), PieceType.BLACK_ROOK);
            }
        }
    }

    private void notifyCastling(Position position) {
        if (position.equals(new Position("e1"))) {
            whiteKingMoved = true;
            return;
        }

        if (position.equals(new Position("e8"))) {
            blackKingMoved = true;
            return;
        }

        if (position.equals(new Position("a1"))) {
            whiteLeftRookMoved = true;
            return;
        }

        if (position.equals(new Position("h1"))) {
            whiteRightRookMoved = true;
            return;
        }

        if (position.equals(new Position("a8"))) {
            blackLeftRookMoved = true;
            return;
        }

        if (position.equals(new Position("h8"))) {
            blackRightRookMoved = true;
        }
    }

    public Board getBoard() {
        return board;
    }

    public void clearHistory() {
        history.clear();
    }

    public void setInitLoad(boolean initLoad) {
        this.initLoad = initLoad;
    }

    public String getInitPreset() {
        return initPreset;
    }

    private static final class MouseHandler extends MouseAdapter {

        public final Model model;
        public final Board board;

        public PieceType grabbedCellPieceType;
        public Position  grabbedCellPosition;

        public boolean successfulGrab;

        public MouseHandler(Model model) {
            this.model = model;
            this.board = model.getBoard();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            Cell pressedCell = (Cell) board.getComponentAt(e.getPoint());

            if (pressedCell.pieceType != PieceType.NONE && model.turn(pressedCell.absolutePieceType)) {
                this.successfulGrab = true;

                board.activePieceImage = ImageReader.get(pressedCell.pieceType);

                this.grabbedCellPieceType = pressedCell.pieceType;
                this.grabbedCellPosition  = pressedCell.position;

                model.showMoves(pressedCell);

                pressedCell.select();
                pressedCell.removePiece();

                this.mouseDragged(e);

                board.repaint();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (successfulGrab) {
                this.successfulGrab = false;

                board.isMouseDragging = false;

                Cell chosenCell = (Cell) board.getComponentAt(e.getPoint());

                if (chosenCell.state == Cell.State.HIGHLIGHTED) {
                    // Valid move, so we execute move
                    board.getCell(chosenCell.position).setPiece(grabbedCellPieceType);

                    model.handleMove(grabbedCellPosition, chosenCell.position, grabbedCellPieceType);

                } else {
                    // Invalid move, so we're getting back
                    board.getCell(grabbedCellPosition).setPiece(grabbedCellPieceType);
                }

                model.restoreAll();

                board.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                board.repaint();
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (successfulGrab) {
                // Look at paint() method in Board class
                board.point    = e.getPoint();
                board.isMouseDragging = true;

                board.setCursor(new Cursor(Cursor.HAND_CURSOR));
                board.repaint();
            }
        }
    }

    private static class LeftArrowKeyHandleAction extends AbstractAction {

        private final Model model;

        public LeftArrowKeyHandleAction(String name, Model model) {
            super(name);
            this.model = model;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            this.model.undoMove();
            this.model.getBoard().repaint();
        }
    }
}
