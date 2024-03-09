package ru.chess.constructor;

import java.io.*;
import java.nio.charset.StandardCharsets;

public final class IO {

    private static final String PATH                = ".\\src\\main\\java\\ru\\chess\\constructor\\out\\";
    private static final String SAVED_FILE_NAME     = "saved.txt";
    private static final String FEN_SAVED_FILE_NAME = "fen.txt";

    private IO() {

    }

    private static void create(String fileName) {
        File file = new File(PATH + fileName);

        try {
            if (file.isFile() && !file.isDirectory()) {
                boolean ignored = file.createNewFile();
            }
        } catch (IOException e) {
            System.err.println("Some IOException");
        }
    }

    public static void write(ConstructorModel model) {
        create(SAVED_FILE_NAME);
        create(FEN_SAVED_FILE_NAME);

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(PATH + SAVED_FILE_NAME),
                StandardCharsets.UTF_8)
        )) {
            writer.write(model.getTextField().getText());

        } catch (FileNotFoundException e) {
            System.err.println("No \"" + SAVED_FILE_NAME + "\" file in out folder");

        } catch (IOException e) {
            System.err.println("Some IOException");
        }

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(PATH + FEN_SAVED_FILE_NAME),
                StandardCharsets.UTF_8)
        )) {
            writer.write(model.getFenTextField().getText());

        } catch (FileNotFoundException e) {
            System.err.println("No \"" + FEN_SAVED_FILE_NAME + "\" file in out folder");

        } catch (IOException e) {
            System.err.println("Some IOException");
        }
    }

    public static void load(ConstructorModel model) {
        String read = "";

        try (BufferedReader reader = new BufferedReader(new FileReader(PATH + SAVED_FILE_NAME))) {
            read = reader.readLine();

        } catch (FileNotFoundException e) {
            System.err.println("No \"" + SAVED_FILE_NAME + "\" file in out folder");

        } catch (IOException e) {

            System.err.println("Some IOException");
        }

        model.getTextField().setText(read);
        model.loadPreset(read);

        String fenRead = "";

        try (BufferedReader reader = new BufferedReader(new FileReader(PATH + FEN_SAVED_FILE_NAME))) {
            fenRead = reader.readLine();

        } catch (FileNotFoundException e) {
            System.err.println("No \"" + FEN_SAVED_FILE_NAME + "\" file in out folder");

        } catch (IOException e) {

            System.err.println("Some IOException");
        }

        model.getFenTextField().setText(fenRead);
    }
}
