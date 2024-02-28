package ru.mathparser;

public final class Token {
    private final TokenType tokenType;
    private final String data;

    public Token(TokenType tokenType, String data) {
        this.tokenType = tokenType;
        this.data = data;
    }

    public Token(TokenType tokenType, Character data) {
        this.tokenType = tokenType;
        this.data = data.toString();
    }

    public TokenType getTokenType() {
        return this.tokenType;
    }

    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Token{" + "type=" + getTokenType() + ", data= '" + getData() + "'}";
    }
}
