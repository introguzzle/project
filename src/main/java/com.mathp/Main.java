package com.mathp;

import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.knowm.xchart.*;

import javax.swing.event.SwingPropertyChangeSupport;

public class Main {

    public static Double _round(Double d, int precise)
    {
        BigDecimal bigDecimal = new BigDecimal(d);
        bigDecimal = bigDecimal.setScale(precise, RoundingMode.HALF_UP);;
        return bigDecimal.doubleValue();
    }

    public static double simpleEval(String s) throws MathParser.SyntaxParseException {
        List<MathParser.Lexeme> _lexeme_list = MathParser.lexParse(s);
        MathParser.LexemeBuffer _lexeme_buff = new MathParser.LexemeBuffer(_lexeme_list);
        return MathParser.Syntax.EXPRESSION(_lexeme_buff);
    }

    public static double functionEval(String s, double value, List<Double> list_of_coeff_values) throws MathParser.SyntaxParseException {
        String variable = new MathParser.FunctionHandle(s).getVariable();
        MathParser.FunctionHandle function = new MathParser.FunctionHandle(s).replaceVariable(value);

        List<String> list_of_coeffs = new ArrayList<>();
        list_of_coeffs.addAll(new MathParser.FunctionHandle(function.expression).getCoeffs(variable).keySet());

        if (list_of_coeffs.size() > 0) {
            for (int i = 0; i < list_of_coeffs.size(); i++) {
                function = function.replaceCoeff(list_of_coeffs.get(i), list_of_coeff_values.get(i));
            }
        }

        List<MathParser.Lexeme> _lexeme_list = MathParser.lexParse(function.toString());
        MathParser.LexemeBuffer _lexeme_buff = new MathParser.LexemeBuffer(_lexeme_list);
        return MathParser.Syntax.EXPRESSION(_lexeme_buff);
    }

    public static double[][] getScaledData(String function, double left, double right, List<Double> _coeffs, double precision)
        throws MathParser.SyntaxParseException {

        double scale = 16.0 / 9.0;
        double step = 1.0 / Math.pow(10, precision);

        List<Double> xvals = new ArrayList<>();
        List<Double> yvals = new ArrayList<>();

        for (double x = left; x < right; x = x + step) {
            xvals.add(x);
            double y = functionEval(function, x, _coeffs);
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

    public static double[][] getData(String function, double left, double right, List<Double> _coeffs)
            throws MathParser.SyntaxParseException {

        double precision = 3;
        double step = (right - left) / Math.pow(10, precision);

        List<Double> xvals = new ArrayList<>();
        List<Double> yvals = new ArrayList<>();

        for (double x = left; x < right; x = x + step) {
            xvals.add(x);
            yvals.add(functionEval(function, x, _coeffs));
        }

        double[] x = new double[xvals.size()];
        double[] y = new double[yvals.size()];

        for (int i = 0; i < xvals.size(); i++) {
            x[i] = xvals.get(i);
            y[i] = yvals.get(i);
        }

        return new double[][] {x, y};
    }

    public static void show(List<Double> _coeffs) {
    }

    public static void main(String[] args) throws MathParser.SyntaxParseException, java.io.IOException, InterruptedException {
        Scanner input = new Scanner(System.in);

        String function = input.nextLine();

        double[][] data = getScaledData(function, -4.0, 4.0, new ArrayList<>(), 5.0);
        double[] x = data[0];
        double[] y = data[1];
        double[] _x = new double[x.length];

        for (int i = 0; i < x.length; i++) {
            _x[i] = _round(x[i], 2);
        }

        XYChart chart = QuickChart.getChart("function", "x", "y", function, _x, y);
        new SwingWrapper(chart).displayChart();
    }
}


