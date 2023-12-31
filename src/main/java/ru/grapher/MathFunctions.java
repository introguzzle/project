package ru.grapher;

import java.util.HashMap;

public final class MathFunctions {

    private MathFunctions() throws ClassNotFoundException {
        throw new ClassNotFoundException("You can't instantiate this class");
    }

    public static HashMap<String, MathFunction> getFunctionMap() {
        HashMap<String, MathFunction> functionMap = new HashMap<>();
        functionMap.put("abs", args -> {
            if (args.size() != 1)
                throw new IllegalArgumentException("Found " + args.size() + " arguments, excepted 1 argument in function abs(arg)");
            else {
                return Math.abs(args.get(0));
            }
        });

        functionMap.put("pow", args -> {
            if (args.size() != 2)
                throw new IllegalArgumentException("Found " + args.size() + " arguments, excepted 2 arguments in function pow(arg, arg)");
            else {
                return Math.pow(args.get(0), args.get(1));
            }
        });

        functionMap.put("sin", args -> {
            if (args.size() != 1)
                throw new IllegalArgumentException("Found " + args.size() + " arguments, excepted 1 argument in function sin(arg)");
            else {
                return Math.sin(args.get(0));
            }
        });

        functionMap.put("cos", args -> {
            if (args.size() != 1)
                throw new IllegalArgumentException("Found " + args.size() + " arguments, excepted 1 argument in function cos(arg)");
            else {
                return Math.cos(args.get(0));
            }
        });

        functionMap.put("tg", args -> {
            if (args.size() != 1)
                throw new IllegalArgumentException("Found " + args.size() + " arguments, excepted at least 1 argument in function sqrt(arg)");
            else {
                return Math.tan(args.get(0));
            }
        });

        functionMap.put("ctg", args -> {
            if (args.size() != 1)
                throw new IllegalArgumentException("Found " + args.size() + " arguments, excepted 1 argument in function ctg(arg)");
            else {
                return 1.0 / Math.tan(args.get(0));
            }
        });

        functionMap.put("log", args -> {
            if (args.size() != 2)
                throw new IllegalArgumentException("Found " + args.size() + " arguments, excepted 2 arguments in function log(arg, arg)");
            else {
                return Math.log(args.get(1)) / Math.log(args.get(0));
            }
        });

        functionMap.put("ln", args -> {
            if (args.size() != 1)
                throw new IllegalArgumentException("Found " + args.size() + " arguments, excepted 1 argument in function ln(arg)");
            else {
                return Math.log(args.get(0));
            }
        });

        functionMap.put("cbrt", args -> {
            if (args.size() != 1)
                throw new IllegalArgumentException("Found " + args.size() + " arguments, excepted 1 argument in function cbrt(arg)");
            else {
                return Math.cbrt(args.get(0));
            }
        });

        functionMap.put("sqrt", args -> {
            if (args.size() != 1)
                throw new IllegalArgumentException("Found " + args.size() + " arguments, excepted 1 argument in function sqrt(arg)");
            else {
                return Math.sqrt(args.get(0));
            }
        });

        functionMap.put("min", args -> {
            if (args.isEmpty())
                throw new IllegalArgumentException("Found 0 arguments, excepted at least 1 argument in function min(args...)");
            if (args.size() == 1)
                return args.get(0);
            double min = args.get(0);
            for (Double value : args)
                if (value < min)
                    min = value;
            return min;
        });

        functionMap.put("max", args -> {
            if (args.isEmpty())
                throw new IllegalArgumentException("Found 0 arguments, excepted at least 1 argument in function max(args...)");
            if (args.size() == 1)
                return args.get(0);
            double max = args.get(0);
            for (Double value : args)
                if (value > max)
                    max = value;
            return max;
        });

        functionMap.put("sq", args -> {
            if (args.size() != 1)
                throw new IllegalArgumentException("Found " + args.size() + " arguments, excepted 1 argument in function sq(arg)");
            return args.get(0) * args.get(0);
        });

        functionMap.put("cb", args -> {
            if (args.size() != 1)
                throw new IllegalArgumentException("Found " + args.size() + " arguments, excepted 1 argument in function cb(arg)");
            return args.get(0) * args.get(0) * args.get(0);
        });

        functionMap.put("arcsin", args -> {
            if (args.size() != 1)
                throw new IllegalArgumentException("Found " + args.size() + " arguments, excepted 1 argument in function arcsin(arg)");
            return java.lang.Math.asin(args.get(0));
        });

        functionMap.put("arccos", args -> {
            if (args.size() != 1)
                throw new IllegalArgumentException("Found " + args.size() + " arguments, excepted 1 argument in function arccos(arg)");
            return java.lang.Math.acos(args.get(0));
        });

        functionMap.put("arctg", args -> {
            if (args.size() != 1)
                throw new IllegalArgumentException("Found " + args.size() + " arguments, excepted 1 argument in function arctg(arg)");
            return java.lang.Math.atan(args.get(0));
        });

        functionMap.put("arctg2", args -> {
            if (args.size() != 2)
                throw new IllegalArgumentException("Found " + args.size() + " arguments, excepted 2 arguments in function arctg2(arg1, arg2)");
            return java.lang.Math.atan2(args.get(0), args.get(1));
        });

        functionMap.put("arcctg", args -> {
            if (args.size() != 1)
                throw new IllegalArgumentException("Found " + args.size() + " arguments, excepted 1 argument in function arcctg(arg)");
            double arg = args.get(0);
            return java.lang.Math.acos(arg / java.lang.Math.sqrt(1 + arg * arg));
        });

        functionMap.put("rand", args -> {
            if (args.size() != 2)
                throw new IllegalArgumentException("Found " + args.size() + " arguments, excepted 2 arguments in function rand(min, max)");
            double left = args.get(0);
            double right = args.get(1);
            return (Math.random() * (right - left) + 1) + left;
        });

        return functionMap;
    }
}
