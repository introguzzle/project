package ru.life;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

class HandleBoard extends JPanel {

    private static final Integer DEFAULT_DELAY = 200;

    private Integer delay;
    private boolean initial = true;

    private static class Label extends DynamicLabel {
        public Label(String name,
                     String movedText,
                     Runnable pressedAction) {
            super(Color.WHITE, Color.GRAY, ImageReader.get(name), movedText, pressedAction);
        }
    }

    private class NextLabel extends Label {
        public NextLabel() {
            super("next.png", "Next", () -> {
                if (initial) {
                    initial = false;
                    model.write();
                }

                model.next();
            });
        }
    }

    private class StartLabel extends Label {
        public StartLabel() {
            super("play.png", "Play", () -> {
                if (initial) {
                    initial = false;
                    model.write();
                }

                model.play(delay == null ? DEFAULT_DELAY : delay);
            });
        }
    }

    private class DelayField extends JTextField {
        public DelayField() {
            this.setText(String.valueOf(DEFAULT_DELAY));
            this.setFont(GUI.FONT);
            this.getDocument().addDocumentListener(new DocumentChangeListener(() -> {
                try {
                    delay = Integer.parseInt(this.getText());
                } catch (Exception ignored) {

                }
            }));

            this.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    int key = e.getKeyCode();

                    if (key == KeyEvent.VK_R)
                        model.showQuarter();

                    if (key == KeyEvent.VK_M)
                        model.mirror();
                }
            });
        }
    }

    private final Model model;

    HandleBoard(Model model) {
        super();

        this.model = model;

        this.setLayout(new GridBagLayout());

        init();
    }

    private void init() {
        DynamicLabel nextButton  = new NextLabel();
        DynamicLabel startButton = new StartLabel();
        DynamicLabel stopButton  = new Label("stop.png",  "Stop",  model::stop);

        DynamicLabel fillButton  = new Label("fill.png",  "Fill",  () -> pauseAction(model::fillRandom));
        DynamicLabel clearButton = new Label("clear.png", "Clear", () -> pauseAction(model::clear));
        DynamicLabel resetButton = new Label("reset.png", "Reset", () -> pauseAction(model::reset));

        DynamicLabel saveButton  = new Label("save.png",  "Save",  model::save);
        DynamicLabel loadButton  = new Label("load.png",  "Load",  model::open);

        JTextField textField = new DelayField();
        textField.setPreferredSize(nextButton.getPreferredSize());

        this.add(saveButton, loadButton,
                nextButton, startButton, stopButton,
                fillButton, resetButton, clearButton,
                textField);
    }

    private void pauseAction(Runnable after) {
        model.stop();
        after.run();
    }

    private void add(JComponent... components) {
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(5, 5, 5, 5);

        for (var component: components) {
            constraints.gridy++;
            this.add(component, constraints);
        }
    }
}
