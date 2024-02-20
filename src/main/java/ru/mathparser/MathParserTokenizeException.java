package ru.mathparser;

public class MathParserTokenizeException extends MathParserException {

    public MathParserTokenizeException() {
    }

    public MathParserTokenizeException(String message) {
        super(message);
    }

    public MathParserTokenizeException(String message, Throwable cause) {
        super(message, cause);
    }
}
