package ru.grapher;

public class MathParserException extends Exception {

    public MathParserException() {
        super();
    }

    public MathParserException(String message) {
        super(message);
    }

    public MathParserException(String message, Throwable cause) {
        super(message, cause);
    }
}
