package ru.mathparser;

import java.util.List;

public final class TokenBuffer {

    private int pos;
    private final List<Token> tokens;

    public TokenBuffer(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Token getNextToken() {
        return getTokens().get(pos++);
    }

    public void returnBack() {
        pos--;
    }

    public int getPos() {
        return pos;
    }

    public List<Token> getTokens() {
        return tokens;
    }
}
