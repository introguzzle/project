package ru.mathparser;

import java.util.List;

public final class TokenBuffer {
    private int pos;
    public List<Token> tokens;

    public TokenBuffer(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Token getNextToken() {
        return tokens.get(pos++);
    }

    public void returnBack() {
        pos--;
    }

    public int getPos() {
        return pos;
    }

}
