package ru.mathparser;

public class MathParserSyntaxException extends MathParserException {

    public MathParserSyntaxException() {
    }

    public MathParserSyntaxException(String message) {
        super(message);
    }

    public MathParserSyntaxException(String message, Throwable cause) {
        super(message, cause);
    }
}
