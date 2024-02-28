package ru.mathparser;

public class MathParserException extends RuntimeException {

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
