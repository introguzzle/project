package ru.mathparser;

public final class Token {
    public TokenType tokenType;
    public String data;

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

    @Override
    public String toString() {
        return "Token{" + "type=" + tokenType + ", data= '" + data + "'}";
    }

}
