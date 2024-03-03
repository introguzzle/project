package ru.chess.model;

import ru.chess.*;
import ru.chess.label.Cell;
import ru.chess.position.Position;

import javax.swing.*;
import java.awt.event.*;
import java.util.Stack;

public class Model extends AbstractModel {

    public enum State {
        ONGOING,
        CHECK_TO_WHITE,
        CHECK_TO_BLACK,
        STALEMATE,
        CHECKMATE_TO_WHITE,
        CHECKMATE_TO_BLACK
    }

    protected boolean initLoad  = true;
    protected String  initPreset;

    public    Stack<String> history = new Stack<>();

    public    State   state = State.ONGOING;
    protected boolean turn  = true;

    // Purpose of all of these variables is Castling and En Passant rules

    protected boolean needWhiteCastlingNotify = true;
    protected boolean needBlackCastlingNotify = true;

    public boolean  whiteKingMoved        = false;
    public boolean  whiteLeftRookMoved    = false;
    public boolean  whiteRightRookMoved   = false;

    public Position whiteKingPosition     ;
    public Position whiteLeftRookPosition ;
    public Position whiteRightRookPosition;

    public boolean  blackKingMoved        = false;
    public boolean  blackLeftRookMoved    = false;
    public boolean  blackRightRookMoved   = false;

    public Position blackKingPosition     ;
    public Position blackLeftRookPosition ;
    public Position blackRightRookPosition;

    public PieceType lastWhiteMovedPieceType = PieceType.NONE;
    public PieceType lastBlackMovedPieceType = PieceType.NONE;

    public boolean lastMoveDestroyed = false;

    public Move lastWhitePawnMove = new Move();
    public Move lastBlackPawnMove = new Move();

    public boolean initPawnPromotion = false;

    //

    public Model(int vertical, int horizontal) {
        super(vertical, horizontal);
        init();
    }

    private void init() {
        MouseHandler mouseHandler = new MouseHandler(this);

        board.addMouseListener(mouseHandler);
        board.addMouseMotionListener(mouseHandler);

        board.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "Left");
        board.getActionMap().put("Left", new LeftArrowKeyHandleAction("Left", this));
    }

    public void reinitialize() {
        history.clear();
        initLoad = true;

        loadPreset(initPreset);

        board.repaint();
    }

    public void loadDefaultPreset() {
        if (isDefaultBoard()) {
            loadPreset(Presets.DEFAULT);
        } else {
            loadPreset(PresetFactory.create());
        }

        PreStartConditions.initCastling(this);
        PreStartConditions.checkPawnPromotion(this);
    }

    public boolean isDefaultBoard() {
        return VERTICAL_BOUND == 8 && HORIZONTAL_BOUND == 8;
    }

    @Override
    public void loadPreset(String preset) {
        if (initLoad) {
            Presets.Loader.load(this, preset);

            history.push(Presets.Reader.read(this));

            initPreset = preset;
            initLoad   = false;

        } else {
            Presets.Loader.load(this, preset);
        }

        PreStartConditions.initCastling(this);
        PreStartConditions.checkPawnPromotion(this);
    }

    public void showMoves(Cell cell) {
        for (Position position: ValidMoves.get(this, cell)) {
            board.getCell(position).highlight();
        }
    }

    public void handleMove(Position  oldPosition,
                           Position  newPosition,
                           PieceType movedPieceType) {

        SoundPlayer.playMoveSound();

        if (movedPieceType == PieceType.WHITE_PAWN || movedPieceType == PieceType.BLACK_PAWN) {
            if (newPosition.getChessHeight() == (movedPieceType.absolute() == AbsolutePieceType.WHITE ? VERTICAL_BOUND : 1))
                MoveHandler.executePawnPromotion(this, newPosition, movedPieceType.absolute());

            MoveHandler.executeEnPassant(this, oldPosition, newPosition, movedPieceType);
            MoveHandler.notifyEnPassant(this, oldPosition, newPosition, movedPieceType);
        }

        if (movedPieceType == PieceType.WHITE_KING || movedPieceType == PieceType.BLACK_KING)
            if (Math.abs(oldPosition.getWidth() - newPosition.getWidth()) == 2)
                MoveHandler.executeCastling(this, newPosition, movedPieceType.absolute());

        MoveHandler.checkGuaranteedStalemate(this, movedPieceType);
        MoveHandler.handleState(this, movedPieceType.absolute().invert());

        MoveHandler.notifyLastMoved(this, movedPieceType);

        turn = !turn;

        history.push(Presets.Reader.read(this));

        if (needWhiteCastlingNotify || needBlackCastlingNotify)
            MoveHandler.notifyCastling(this, newPosition);

        if (state == State.CHECKMATE_TO_WHITE || state == State.CHECKMATE_TO_BLACK || state == State.STALEMATE) {
            MoveHandler.callMateDialog(this, movedPieceType);
        }
    }

    private void undoMove() {
        if (history.size() > 1) {
            history.pop();
            SoundPlayer.playMoveSound();
            loadPreset(history.peek());
        } else {
            loadPreset(history.firstElement());
        }
    }

    public boolean turn(AbsolutePieceType absolutePieceType) {
        return turn == (absolutePieceType == AbsolutePieceType.WHITE);
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
