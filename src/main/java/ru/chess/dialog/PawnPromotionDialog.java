package ru.chess.dialog;

import ru.chess.AbsolutePieceType;
import ru.chess.Chess;
import ru.chess.gui.ImageReader;
import ru.chess.PieceType;
import ru.chess.gui.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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

    public static final class PawnPromotionPanel extends JPanel {

        public final Color DEFAULT_COLOR;

        private static final class ChoiceLabel extends JLabel {

            public final PieceType pieceType;

            public ChoiceLabel(PawnPromotionPanel panel, PieceType pieceType) {
                this.pieceType = pieceType;
                this.setIcon(ImageReader.get(pieceType));

                MouseHandler mouseHandler = new MouseHandler(panel);

                this.setBackground(panel.DEFAULT_COLOR);
                this.setOpaque(true);
                this.setBorder(BorderFactory.createEmptyBorder());

                this.addMouseListener(mouseHandler);
                this.addMouseMotionListener(mouseHandler);
            }

            @Override
            public void paint(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                GUI.setQuality(g2d, 2);
                super.paint(g2d);
            }

        }

        public final JFrame owner;

        public final AbsolutePieceType pawnPieceType;
        public final boolean           isPawnWhite;

        public       PieceType         chosenPieceType;

        public PawnPromotionPanel(JFrame owner, AbsolutePieceType pawnPieceType) {
            this.owner = owner;

            this.pawnPieceType = pawnPieceType;
            this.isPawnWhite   = pawnPieceType == AbsolutePieceType.WHITE;
            this.DEFAULT_COLOR = this.getBackground();

            init();
        }

        private void init() {
            this.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));

            JLabel knightLabel = new ChoiceLabel(this, isPawnWhite ? PieceType.WHITE_KNIGHT : PieceType.BLACK_KNIGHT);
            JLabel bishopLabel = new ChoiceLabel(this, isPawnWhite ? PieceType.WHITE_BISHOP : PieceType.BLACK_BISHOP);
            JLabel rookLabel   = new ChoiceLabel(this, isPawnWhite ? PieceType.WHITE_ROOK   : PieceType.BLACK_ROOK);
            JLabel queenLabel  = new ChoiceLabel(this, isPawnWhite ? PieceType.WHITE_QUEEN  : PieceType.BLACK_QUEEN);

            if (!((Chess) owner).getModel().isDefaultBoard()) {
                JLabel clownLabel    = new ChoiceLabel(this, isPawnWhite ? PieceType.WHITE_CLOWN    : PieceType.BLACK_CLOWN);
                JLabel tamplierLabel = new ChoiceLabel(this, isPawnWhite ? PieceType.WHITE_TAMPLIER : PieceType.BLACK_TAMPLIER);
                JLabel wizardLabel   = new ChoiceLabel(this, isPawnWhite ? PieceType.WHITE_WIZARD   : PieceType.BLACK_WIZARD);

                this.add(clownLabel);
                this.add(tamplierLabel);
                this.add(wizardLabel);
            }

            this.add(knightLabel);
            this.add(bishopLabel);
            this.add(rookLabel);
            this.add(queenLabel);
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            GUI.setQuality(g2d, 2);
            super.paint(g2d);
        }

        private static final class MouseHandler extends MouseAdapter {

            private final PawnPromotionPanel pawnPromotionPanel;

            public MouseHandler(PawnPromotionPanel pawnPromotionPanel) {
                this.pawnPromotionPanel = pawnPromotionPanel;
            }

            @Override
            public void mousePressed(MouseEvent e) {
                this.pawnPromotionPanel.chosenPieceType = ((ChoiceLabel) e.getSource()).pieceType;

                JDialog master = (JDialog) SwingUtilities.getWindowAncestor(this.pawnPromotionPanel);

                master.setVisible(false);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                JLabel label = (JLabel) e.getSource();

                label.setBackground(Color.WHITE.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                JLabel label = (JLabel) e.getSource();

                label.setBackground(pawnPromotionPanel.DEFAULT_COLOR);
            }
        }
    }
}
