package ru.grapher;

public class Main {

    public static double parse(String expression) throws MathParser.SyntaxParseException {
        return MathParser.parse(expression);
    }

    public static void main(String[] args) throws MathParser.SyntaxParseException, java.io.IOException, InterruptedException {
    }
}


