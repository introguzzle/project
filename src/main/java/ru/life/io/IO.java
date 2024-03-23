package ru.life.io;

import javax.swing.*;
import java.io.*;

public class IO {

    private static final String PATH = ".\\src\\main\\java\\ru\\life\\out\\saved.txt";

    public static void write(boolean[][] matrix) {
        writeTo(PATH, matrix);
    }

    public static void writeTo(String name, boolean[][] matrix) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(name))) {
            for (boolean[] row : matrix) {
                for (boolean state : row) {
                    writer.write(state ? '1' : '0');
                }

                writer.newLine();
            }
        } catch (IOException e) {
            print(e);
        }
    }

    public static boolean[][] load(int i, int j) {
        return loadFrom(PATH, i, j);
    }

    public static boolean[][] loadFrom(String path, int i, int j) {
        boolean[][] result = new boolean[i][j];

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            int row = 0;
            while ((line = reader.readLine()) != null && row < i) {
                for (int col = 0; col < Math.min(j, line.length()); col++) {
                    result[row][col] = line.charAt(col) == '1';
                }

                row++;
            }
        } catch (IOException e) {
            print(e);
        }

        return result;
    }

    public static void save(JFrame owner, boolean[][] matrix) {
        setSystemLAF();

        SaveChooser chooser = new SaveChooser(owner);

        int selection = chooser.showDialog();

        if (selection == JFileChooser.APPROVE_OPTION) {
            String filePath = chooser.getPath();

            try {
                IO.writeTo(filePath, matrix);
            } catch (RuntimeException e) {
                print(e);
            }
        }
    }

    public static boolean[][] open(JFrame owner, int i, int j) {
        setSystemLAF();

        OpenChooser chooser = new OpenChooser(owner);

        int selection = chooser.showDialog();

        if (selection == JFileChooser.APPROVE_OPTION) {
            String filePath = chooser.getPath();

            try {
                return IO.loadFrom(filePath, i, j);
            } catch (RuntimeException e) {
                print(e);
            }
        }

        return null;
    }

    private static void print(Exception e) {
        System.err.println("Failed IO operation");
        System.err.println(e.getMessage());
    }

    private static void setSystemLAF() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException ignored) {

        }
    }
}
