package ru.chess.constructor;

import ru.chess.PieceType;
import ru.chess.label.ChoiceCell;
import ru.chess.gui.GUI;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class ConstructorChoicePanel extends JPanel {

    private final int vertical;

    public ChoiceCell[][]  cells;
    public List<PieceType> allPieces = new ArrayList<>();

    {
        allPieces.addAll(Arrays.asList(PieceType.values()));
        allPieces.remove(PieceType.NONE);
    }

    public ImageIcon activePieceImage;
    public Point     point;
    public boolean   drawPiece;

    public ConstructorChoicePanel(int vertical) {
        super(true);
        this.vertical = vertical;
        init();
    }

    public void init() {
        cells = new ChoiceCell[vertical][allPieces.size()];

        this.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();

        int k = 0;

        for (int j = 0; j < allPieces.size(); j++) {
            for (int i = 0; i < vertical; i++) {
                constraints.gridx = j;
                constraints.gridy = i;

                try {
                    cells[i][j] = new ChoiceCell(allPieces.get(k));
                } catch (IndexOutOfBoundsException ignored) {
                    break;
                }

                this.add(cells[i][j], constraints);
                k++;
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        GUI.setQuality(g2d, 2);

        super.paint(g2d);

        if (drawPiece) {
            Image pieceImage = activePieceImage.getImage();

            int dx = point.x - pieceImage.getHeight(this) / 2;
            int dy = point.y - pieceImage.getWidth(this) / 2;

            g2d.drawImage(pieceImage, dx, dy, this);
        }
    }
}
