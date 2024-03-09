package ru.chess.dialog;

import ru.chess.AbsolutePieceType;
import ru.chess.Chess;
import ru.chess.label.Action;
import ru.chess.label.DynamicLabel;
import ru.chess.gui.ImageReader;
import ru.chess.PieceType;
import ru.chess.gui.GUI;

import javax.swing.*;
import java.awt.*;

public final class PawnPromotionDialog extends JDialog {

    public final JFrame owner;
    public final PawnPromotionPanel pawnPromotionPanel;

    public PawnPromotionDialog(JFrame owner, AbsolutePieceType pawnType) {
        super(owner, "Choose a piece", true);

        this.owner = owner;
        this.pawnPromotionPanel = new PawnPromotionPanel(owner, pawnType);

        this.add(pawnPromotionPanel);
        this.pack();
        this.setLocationRelativeTo(null);

        this.setResizable(false);
        this.setIconImage(owner.getIconImage());
    }

    public static final class ChoiceLabel extends DynamicLabel {

        public final PieceType pieceType;
        public final PawnPromotionPanel panel;

        /**
         * @param pieceType     Piece type of this label
         * @param pressedAction What to perform when pressed
         */
        public ChoiceLabel(PawnPromotionPanel panel,
                           PieceType pieceType,
                           Action pressedAction) {
            super(
                    new Color(238, 238, 238),
                    new Color(200, 200, 190),
                    ImageReader.get(pieceType),
                    "",
                    pressedAction);

            this.panel = panel;
            this.pieceType = pieceType;
        }
    }

    public static final class PawnPromotionPanel extends JPanel {

        public final JFrame owner;

        public final AbsolutePieceType pawnPieceType;
        public final boolean           whitePawn;

        public       PieceType         chosenPieceType;

        public PawnPromotionPanel(JFrame owner, AbsolutePieceType pawnPieceType) {
            super(true);
            this.owner = owner;

            this.pawnPieceType = pawnPieceType;
            this.whitePawn     = pawnPieceType.isWhite();

            init();
        }

        private void init() {
            this.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));

            Action chooseAction = e -> {
                ChoiceLabel label = (ChoiceLabel) e.getSource();
                label.panel.chosenPieceType = ((ChoiceLabel) e.getSource()).pieceType;
                JDialog master = (JDialog) SwingUtilities.getWindowAncestor(label.panel);

                master.setVisible(false);
            };

            boolean defaultBoard = ((Chess) owner).getModel().isDefaultBoard();

            for (var pieceType: (whitePawn ? PieceType.allWhites() : PieceType.allBlacks()))
                if (!pieceType.name().contains("KING"))
                    if (!defaultBoard) {
                        JLabel choiceLabel = new ChoiceLabel(this, pieceType, chooseAction);
                        this.add(choiceLabel);

                    } else if (!pieceType.extended) {
                        JLabel choiceLabel = new ChoiceLabel(this, pieceType, chooseAction);
                        this.add(choiceLabel);
                    }
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            GUI.setQuality(g2d, 2);
            super.paint(g2d);
        }
    }
}
