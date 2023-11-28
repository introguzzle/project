package com.mathp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.knowm.xchart.*;

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
        MathParser.FunctionHandle function = new MathParser.FunctionHandle(s).replaceVariable(value);

        List<String> list_of_coeffs = new ArrayList<>();
        list_of_coeffs.addAll(new MathParser.FunctionHandle(function.expression).getCoeffs("x").keySet());

        System.out.println(list_of_coeffs);

        for (int i = 0; i < list_of_coeffs.size(); i++) {
            function = function.replaceCoeff(list_of_coeffs.get(i), list_of_coeff_values.get(i));
        }

        System.out.println("lex test = " + function);

        List<MathParser.Lexeme> _lexeme_list = MathParser.lexParse(function.toString());
        MathParser.LexemeBuffer _lexeme_buff = new MathParser.LexemeBuffer(_lexeme_list);
        return MathParser.Syntax.EXPRESSION(_lexeme_buff);
    }

    public static void main(String[] args) throws MathParser.SyntaxParseException, java.io.IOException {
        Scanner input = new Scanner(System.in);

        // f(x) = max(A, x, B)
        String function = input.nextLine();

        List<Double> coeffs = new ArrayList<>(Arrays.asList(1.0, 2.0));

        List<Double> xvals = new ArrayList<>();

        List<Double> yvals = new ArrayList<>();

        for (double x = 0.1; x < 10.0; x = x + 0.1) {
            xvals.add(_round(x, 2));
            yvals.add(_round(functionEval(function, x, coeffs), 2));
        }

        double[] xData = new double[xvals.size()];
        double[] yData = new double[yvals.size()];

        for (int index = 0; index < xvals.size(); index++) {
            xData[index] = xvals.get(index);
            yData[index] = yvals.get(index);
        }

// Create Chart
        XYChart chart = QuickChart.getChart("Sample Chart", "X", "Y", "y(x)", xData, yData);

// Show it
        new SwingWrapper(chart).displayChart();

// Save it
        BitmapEncoder.saveBitmap(chart, "./Sample_Chart", BitmapEncoder.BitmapFormat.PNG);

// or save it in high-res
        BitmapEncoder.saveBitmapWithDPI(chart, "./Sample_Chart_300_DPI", BitmapEncoder.BitmapFormat.PNG, 300);

    }
}

