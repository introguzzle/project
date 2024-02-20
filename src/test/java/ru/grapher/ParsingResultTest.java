package ru.grapher;

import org.junit.Test;
import ru.mathparser.MathParser;
import ru.mathparser.ParsingResult;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParsingResultTest {

    @Test
    public void test() {
        assertEquals(MathParser.getParsingResult("5"),
                ParsingResult.EXPRESSION);
        assertEquals(MathParser.getParsingResult("5 + 3"),
                ParsingResult.EXPRESSION);
        assertEquals(MathParser.getParsingResult("9 ^ 9"),
                ParsingResult.EXPRESSION);
        assertEquals(MathParser.getParsingResult("pi + e + gr"),
                ParsingResult.EXPRESSION);

        assertEquals(MathParser.getParsingResult("f(x) = sin(x)"),
                ParsingResult.EXPLICIT_FUNCTION);
        assertEquals(MathParser.getParsingResult("f(x) = cos(ln(ln(x)))"),
                ParsingResult.EXPLICIT_FUNCTION);
        assertEquals(MathParser.getParsingResult("f(x) = log(x, x)"),
                ParsingResult.EXPLICIT_FUNCTION);
        assertEquals(MathParser.getParsingResult("f(x) = pow(x, pi)"),
                ParsingResult.EXPLICIT_FUNCTION);
        assertEquals(MathParser.getParsingResult("f(x) = pi ^ e"),
                ParsingResult.EXPLICIT_FUNCTION);

        assertEquals(MathParser.getParsingResult("f(x) = a + b"),
                ParsingResult.EXPLICIT_FUNCTION_WITH_PARAMS);
        assertEquals(MathParser.getParsingResult("f(x) = pi - c - d"),
                ParsingResult.EXPLICIT_FUNCTION_WITH_PARAMS);
        assertEquals(MathParser.getParsingResult("f(x) = pi + r"),
                ParsingResult.EXPLICIT_FUNCTION_WITH_PARAMS);
        assertEquals(MathParser.getParsingResult("f(x) = sin(a) - sin(b) / tg(a - x)"),
                ParsingResult.EXPLICIT_FUNCTION_WITH_PARAMS);
        assertEquals(MathParser.getParsingResult("f(x) = ln(ln(d)) + x / 2.34"),
                ParsingResult.EXPLICIT_FUNCTION_WITH_PARAMS);

        assertEquals(MathParser.getParsingResult("t"),
                ParsingResult.PARAMETRIC_FUNCTION);
        assertEquals(MathParser.getParsingResult("sin(t)"),
                ParsingResult.PARAMETRIC_FUNCTION);
        assertEquals(MathParser.getParsingResult("t - pi"),
                ParsingResult.PARAMETRIC_FUNCTION);
        assertEquals(MathParser.getParsingResult("t / t + ln(t) + erf(t)"),
                ParsingResult.PARAMETRIC_FUNCTION);

        assertEquals(MathParser.getParsingResult("t - a"),
                ParsingResult.PARAMETRIC_FUNCTION_WITH_PARAMS);
        assertEquals(MathParser.getParsingResult("d"),
                ParsingResult.PARAMETRIC_FUNCTION_WITH_PARAMS);
        assertEquals(MathParser.getParsingResult("j - i / t"),
                ParsingResult.PARAMETRIC_FUNCTION_WITH_PARAMS);
        assertEquals(MathParser.getParsingResult("t ^ t ^ sin(t / a)"),
                ParsingResult.PARAMETRIC_FUNCTION_WITH_PARAMS);


    }

}
