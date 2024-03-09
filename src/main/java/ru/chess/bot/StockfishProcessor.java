package ru.chess.bot;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StockfishProcessor {

    private static final String EXECUTABLE_PATH = ".\\src\\main\\java\\ru\\chess\\engine\\stockfish.exe";

    private final Process process;

    private final BufferedReader input;
    private final BufferedWriter output;

    public StockfishProcessor() {
        try {
            process = Runtime.getRuntime().exec(EXECUTABLE_PATH);
            input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setOption("Contempt", 75);
        setOption("Threads", Math.max(1, Runtime.getRuntime().availableProcessors() / 2));
        setOption("Hash", 64);
    }

    public void close() {
        sendCommand("quit");
        process.destroy();
        try {
            input.close();
            output.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getFen() {
        waitForReady();
        sendCommand("d");

        String fen = "";
        List<String> response = readResponse("Checkers:");

        for (int i = response.size() - 1; i >= 0; i--) {
            String line = response.get(i);
            if (line.startsWith("Fen: ")) {
                fen = line.substring("Fen: ".length());
                break;
            }
        }

        return fen;
    }

    private void waitForReady() {
        sendCommand("isready");
        readResponse("readyok");
    }

    private void sendCommand(String command) {
        try {
            output.write(command + "\n");
            output.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String evaluate(String fen) {
        waitForReady();
        sendCommand("position fen " + fen);

        sendCommand("eval");

        return readResponse("Total Evaluation").getLast();
    }

    private List<String> readResponse(String expected) {
        try {
            List<String> lines = new ArrayList<>();

            while (true) {
                String line = input.readLine();

                lines.add(line);

                if (line.startsWith(expected))
                    break;
            }

            return lines;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setOption(String name, int value) {
        sendCommand("setoption name " + name + " value " + value);
    }

    public String getBestMove(String fen, int difficulty, int milliseconds) {
        waitForReady();
        setOption("Skill Level", difficulty);

        waitForReady();
        sendCommand("position fen " + fen);

        waitForReady();
        sendCommand("go movetime " + milliseconds);

        String bestmove = "";
        List<String> response = readResponse("bestmove");

        for (int i = response.size() - 1; i >= 0; i--) {
            String line = response.get(i);
            if (line.startsWith("bestmove")) {
                bestmove = line.substring("bestmove ".length());
                break;
            }
        }

        return bestmove.split("\\s+")[0];
    }

    private void setFen(String fen) {
        waitForReady();
        sendCommand("position fen " + fen);
    }
}
