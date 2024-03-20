package ru.mathparser;

public enum ParsingResult {
    ERROR,
    EXPRESSION,
    EXPLICIT_FUNCTION,
    EXPLICIT_FUNCTION_WITH_PARAMETERS,
    IMPLICIT_FUNCTION,
    IMPLICIT_FUNCTION_WITH_PARAMETERS,
    PARAMETRIC_FUNCTION,
    PARAMETRIC_FUNCTION_WITH_PARAMETERS;

    public boolean isError() {
        return this == ERROR;
    }

    public boolean isExpression() {
        return this == EXPRESSION;
    }

    public boolean isExplicit() {
        return this.name().contains("EXPLICIT");
    }

    public boolean isParametric() {
        return this.name().contains("PARAMETRIC");
    }

    public boolean hasCoefficients() {
        return this.name().contains("PARAMETERS");
    }
}
