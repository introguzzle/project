package ru.life.io;

import ru.life.Model;

public class IOSupport {
    public static boolean[][] toMatrix(Model model) {
        int size = model.getSize();

        boolean[][] result = new boolean[size][size];

        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                result[i][j] = model.getCells()[i][j].isAlive();

        return result;
    }
}
