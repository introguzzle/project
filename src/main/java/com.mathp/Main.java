package com.mathp;

import javax.swing.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.*;

import static com.mathp.MathParser.FunctionHandle.*;

public class Main {

    public static ArrayList<Double> threadedGetYData(String function, HashMap<String, Double> coefficientMap) {
        return new ArrayList<>();
    }

    public static ArrayList<Double> threadedGetXData(int precisionDigits, String threadUsage) {
        return new ArrayList<>();
    }

    public static Double _round(Double d, int precise) {

        BigDecimal bigDecimal = new BigDecimal(d);
        bigDecimal = bigDecimal.setScale(precise, RoundingMode.HALF_UP);;
        return bigDecimal.doubleValue();
    }

    public static double compute(String expression) throws MathParser.SyntaxParseException {

        List<MathParser.Lexeme> _lexemeList = MathParser.lexParse(replaceConstants(expression));
        MathParser.LexemeBuffer _lexemeBuffer = new MathParser.LexemeBuffer(_lexemeList);
        return MathParser.Syntax.EXPRESSION(_lexemeBuffer);
    }

    public static double compute(String expression, double value, List<Double> listOfCoeffValues) throws MathParser.SyntaxParseException {

        String variable = getVariable(expression);
        expression = replaceVariable(expression, value);
        expression = replaceConstants(expression);

        List<String> listOfCoeffs = new ArrayList<>(getCoeffs(expression, variable).keySet());


        if (listOfCoeffs.size() > 0) {
            for (int i = 0; i < listOfCoeffs.size(); i++) {
                expression = replaceCoeff(expression, listOfCoeffs.get(i), listOfCoeffValues.get(i));
            }
        }

        List<MathParser.Lexeme> _lexemeList = MathParser.lexParse(expression);
        MathParser.LexemeBuffer _lexemeBuffer = new MathParser.LexemeBuffer(_lexemeList);
        return MathParser.Syntax.EXPRESSION(_lexemeBuffer);
    }

    private static boolean containsAnyOf(HashMap<String, Double> map, List<String> list) {
        for (int i = 0; i < list.size(); i++) {
            if (map.containsKey(list.get(i)))
                return true;
        }
        return false;
    }

    public static double compute(String expression, double value, HashMap<String, Double> map) throws MathParser.SyntaxParseException {

        expression = replaceVariable(expression, value);
        expression = replaceConstants(expression);

        List<String> coeffsOfThisExpression = new ArrayList<>(getCoeffs(expression).keySet());

        if (!map.isEmpty()) {
            for (Map.Entry<String, Double> entry : map.entrySet()) {
                String _K = entry.getKey();
                double _V = entry.getValue();
                if (containsAnyOf(map, coeffsOfThisExpression))
                    expression = replaceCoeff(expression, _K, _V);
            }
        }

        List<MathParser.Lexeme> _lexemeList = MathParser.lexParse(expression);
        MathParser.LexemeBuffer _lexemeBuffer = new MathParser.LexemeBuffer(_lexemeList);
        return MathParser.Syntax.EXPRESSION(_lexemeBuffer);
    }

    public static double[][] getScaledData(String function, double left, double right, List<Double> _coeffs, double precision)
        throws MathParser.SyntaxParseException {

        double scale = 16.0 / 9.0;
        double step = 1.0 / Math.pow(10, precision);

        List<Double> xvals = new ArrayList<>();
        List<Double> yvals = new ArrayList<>();

        for (double x = left; x < right; x = x + step) {
            xvals.add(x);
            double y = compute(function, x, _coeffs);
            if (y < left / scale | y > right / scale) {
                if (y <= left / scale)
                    yvals.add(Double.NaN);
                if (y >= right / scale)
                    yvals.add(Double.NaN);
            }
            else
                yvals.add(y);
        }

        double[] x = new double[xvals.size()];
        double[] y = new double[yvals.size()];

        for (int i = 0; i < xvals.size(); i++) {
            x[i] = xvals.get(i);
            y[i] = yvals.get(i);
        }

        return new double[][] {x, y};
    }

    public static double[][] getData(String function, double left, double right, List<Double> _coeffs, int precision)
            throws MathParser.SyntaxParseException {

        double step = 1 / Math.pow(10, (double)precision);

        List<Double> xvals = new ArrayList<>();
        List<Double> yvals = new ArrayList<>();

        for (double x = left; x < right; x = x + step) {
            xvals.add(x);
            yvals.add(compute(function, x, _coeffs));
        }

        double[] x = new double[xvals.size()];
        double[] y = new double[yvals.size()];

        for (int i = 0; i < xvals.size(); i++) {
            x[i] = xvals.get(i);
            y[i] = yvals.get(i);
        }

        return new double[][] {x, y};
    }

    public static double[][] getOptimizedData(String function, HashMap<String, Double> coefficientMap, int precisionDigits)
            throws MathParser.SyntaxParseException {

        double left = Graph.getComputationRange().getLowerBound();
        double right = Graph.getComputationRange().getUpperBound();

        int size = 1;

        for (int i = 0; i < precisionDigits; i++) {
            size *= 10;
        }

        double step = 1.0 / (double)size;

        double[] yData = new double[(int)(right - left) * size];
        double[] xData = new double[(int)(right - left) * size];
        int i = 0;

        for (double x = left; x < right; x = x + step) {
            yData[i] = compute(function, x, coefficientMap);
            xData[i] = x;
            i++;
        }
        return new double[][]{xData, yData};
    }

    public static void main(String[] args) throws MathParser.SyntaxParseException, java.io.IOException, InterruptedException {


        //////////////////////////////////////////////////////////////////////////
        //                                                                      //
        // RULES                                                                //
        // constants start with MathParser.FunctionHandle.MATH_CONSTANTS_PREFIX //
        //                                                                      //
        //                                                                      //
        //////////////////////////////////////////////////////////////////////////

        // f(x) = sq(x - c) + pow(x, mp_pi) - A * B

        IndexedMap<Integer, String, Double> m = new IndexedMap<>(10, "test", 2.0);

        m.putAll(new IndexedMap<>(20, "putall", 3.0));

        m.forEach((k, v1, v2) -> System.out.println(k + v1 + v2));
    }
}


