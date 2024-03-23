package ru.life;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

class ImageReader {

    private static final String BASE_PATH = ".\\src\\main\\java\\ru\\life\\images\\";

    static ImageIcon get(String name) {
        return get(name, GUI.LABEL_DIMENSION.width, GUI.LABEL_DIMENSION.height);
    }

    static ImageIcon get(String name, int width, int height) {
        return new ImageIcon(read(name).getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }

    private static Image read(String name) {
        try {
            return ImageIO.read(new File(BASE_PATH + name));
        } catch (IOException ignored) {
            throw new RuntimeException();
        }
    }
}
