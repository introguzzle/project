package ru.mathparser;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.special.Gamma;

public final class MathFunctions {

    private MathFunctions() throws InstantiationException {
        throw new InstantiationException();
    }

    private static final Double  NAN       = Double.NaN;
    private static final boolean CONSTRAIN = true;
    private static final double  MAX_VALUE = 80.0;

    public static final Map<String, String>       REQUIRED_ARGS = new HashMap<>();
    public static final Map<String, MathFunction> FUNCTION_MAP  = new HashMap<>();
    public static final List<String>              SORTED_NAMES  = new ArrayList<>();

    private static String message(int given, int excepted, String function) {
        StringBuilder sb = new StringBuilder();

        String givenArgs = given == 1 ? "argument" : "arguments";
        String exceptedArgs = excepted == 1 || excepted == -1 ? "argument" : "arguments";

        String args = "";

        if (excepted == -1)
            args = "(args...)";
        if (excepted == 0)
            args = "()";
        if (excepted == 1)
            args = "(arg)";
        if (excepted == 2)
            args = "(arg, arg)";

        sb
                .append("Found ")
                .append(given)
                .append(" ")
                .append(givenArgs)

                .append(", excepted ")
                .append(excepted == -1 ? "at least 1" : excepted)
                .append(" ")
                .append(exceptedArgs)
                .append(" in function ")
                .append(function)
                .append(args);

        return sb.toString();
    }

    private static IllegalArgumentException ex(int given, String function) {
        String rq = REQUIRED_ARGS.get(function);

        return new IllegalArgumentException(message(
                given,
                rq.charAt(0) == '+' ? -1 : Integer.parseInt(rq),
                function
        ));
    }

    static {
        REQUIRED_ARGS.put("abs", "1");
        REQUIRED_ARGS.put("pow", "2");
        REQUIRED_ARGS.put("cyclepow", "+2");

        REQUIRED_ARGS.put("sin", "1");
        REQUIRED_ARGS.put("cos", "1");
        REQUIRED_ARGS.put("tg", "1");
        REQUIRED_ARGS.put("ctg", "1");

        REQUIRED_ARGS.put("arcsin", "1");
        REQUIRED_ARGS.put("arccos", "1");
        REQUIRED_ARGS.put("arctg", "1");
        REQUIRED_ARGS.put("arcctg", "1");

        REQUIRED_ARGS.put("log", "2");
        REQUIRED_ARGS.put("ln", "1");

        REQUIRED_ARGS.put("sqrt", "1");
        REQUIRED_ARGS.put("cbrt", "1");
        REQUIRED_ARGS.put("nroot", "2");

        REQUIRED_ARGS.put("min", "+1");
        REQUIRED_ARGS.put("max", "+1");

        REQUIRED_ARGS.put("sq", "1");
        REQUIRED_ARGS.put("cb", "1");

        REQUIRED_ARGS.put("sh", "1");
        REQUIRED_ARGS.put("ch", "1");
        REQUIRED_ARGS.put("th", "1");
        REQUIRED_ARGS.put("cth", "1");
        REQUIRED_ARGS.put("sch", "1");
        REQUIRED_ARGS.put("csch", "1");

        REQUIRED_ARGS.put("arsh", "1");
        REQUIRED_ARGS.put("arch", "1");
        REQUIRED_ARGS.put("arth", "1");
        REQUIRED_ARGS.put("arcth", "1");
        REQUIRED_ARGS.put("arsch", "1");
        REQUIRED_ARGS.put("arcsch", "1");

        REQUIRED_ARGS.put("productlog", "1");

        REQUIRED_ARGS.put("randr", "2");
        REQUIRED_ARGS.put("rand", "1");

        REQUIRED_ARGS.put("gaussrand", "2");

        REQUIRED_ARGS.put("gaussdensity", "1");
        REQUIRED_ARGS.put("gaussdensityp", "3");

        REQUIRED_ARGS.put("gamma", "1");
        REQUIRED_ARGS.put("digamma", "1");
        REQUIRED_ARGS.put("trigamma", "1");

        REQUIRED_ARGS.put("erf", "1");
        REQUIRED_ARGS.put("erfinv", "1");
    }



    static {
        FUNCTION_MAP.put("abs", args -> {
            if (args.size() != 1) throw ex(args.size(), "abs");
            return bounded(Math.abs(args.getFirst()));
        });

        FUNCTION_MAP.put("pow", args -> {
            if (args.size() != 2) throw ex(args.size(), "pow");
            return bounded(pow(args.get(0), args.get(1)));
        });

        FUNCTION_MAP.put("cyclepow", args -> {
            if (args.size() < 2) throw ex(args.size(), "cyclepow");

            double value = args.getLast();

            for (int i = args.size() - 2; i >= 0; i--) {
                value = Math.pow(args.get(i), value);
            }

            return value;
        });

        FUNCTION_MAP.put("sin", args -> {
            if (args.size() != 1) throw ex(args.size(), "sin");
            return bounded(Math.sin(args.getFirst()));
        });

        FUNCTION_MAP.put("cos", args -> {
            if (args.size() != 1) throw ex(args.size(), "cos");
            return bounded(Math.cos(args.getFirst()));

        });

        FUNCTION_MAP.put("tg", args -> {
            if (args.size() != 1) throw ex(args.size(), "tg");
            return bounded(Math.tan(args.getFirst()));

        });

        FUNCTION_MAP.put("ctg", args -> {
            if (args.size() != 1) throw ex(args.size(), "ctg");
            return bounded(1.0 / Math.tan(args.getFirst()));
        });

        FUNCTION_MAP.put("sh", args -> {
            if (args.size() != 1) throw ex(args.size(), "sh");

            final double x = args.getFirst();
            return bounded((Math.sinh(x)));
        });

        FUNCTION_MAP.put("ch", args -> {
            if (args.size() != 1) throw ex(args.size(), "ch");

            final double x = args.getFirst();

            return bounded(Math.cosh(x));

        });

        FUNCTION_MAP.put("th", args -> {
            if (args.size() != 1) throw ex(args.size(), "th");
            return bounded(Math.tanh(args.getFirst()));
        });

        FUNCTION_MAP.put("cth", args -> {
            if (args.size() != 1) throw ex(args.size(), "cth");
            return bounded(1.0 / Math.tanh(args.getFirst()));
        });

        FUNCTION_MAP.put("sch", args -> {
            if (args.size() != 1) throw ex(args.size(), "sch");
            return bounded(1.0 / FUNCTION_MAP.get("sh").apply(args));
        });

        FUNCTION_MAP.put("csch", args -> {
            if (args.size() != 1) throw ex(args.size(), "csch");
            return bounded(1.0 / FUNCTION_MAP.get("ch").apply(args));
        });

        FUNCTION_MAP.put("arsh", args -> {
            if (args.size() != 1) throw ex(args.size(), "arsh");
            final double x = args.getFirst();

            return bounded(Math.log(x + Math.sqrt(x * x + 1)));
        });

        FUNCTION_MAP.put("arch", args -> {
            if (args.size() != 1) throw ex(args.size(), "arch");
            final double x = args.getFirst();

            return bounded(Math.log(x + Math.sqrt(x * x - 1)));
        });

        FUNCTION_MAP.put("arth", args -> {
            if (args.size() != 1) throw ex(args.size(), "arth");
            final double x = args.getFirst();

            return bounded(0.5 * Math.log((1.0 + x) / (1.0 - x)));
        });

        FUNCTION_MAP.put("arcth", args -> {
            if (args.size() != 1) throw ex(args.size(), "arcth");
            final double x = args.getFirst();

            return bounded(0.5 * Math.log((x + 1.0) / (x - 1.0)));

        });

        FUNCTION_MAP.put("arsch", args -> {
            if (args.size() != 1) throw ex(args.size(), "arsch");
            final double x = args.getFirst();

            return bounded(Math.log(1.0 / x + Math.sqrt(1 / (x * x) - 1)));

        });

        FUNCTION_MAP.put("arcsch", args -> {
            if (args.size() != 1) throw ex(args.size(), "arcsch");
            final double x = args.getFirst();

            return bounded(Math.log(1.0 / x + Math.sqrt(1 / (x * x) + 1)));
        });

        FUNCTION_MAP.put("log", args -> {
            if (args.size() != 2) throw ex(args.size(), "log");
            return bounded(Math.log(args.getLast()) / Math.log(args.getFirst()));
        });

        FUNCTION_MAP.put("ln", args -> {
            if (args.size() != 1) throw ex(args.size(), "ln");
            return bounded(Math.log(args.getFirst()));
        });

        FUNCTION_MAP.put("cbrt", args -> {
            if (args.size() != 1) throw ex(args.size(), "cbrt");
            return bounded(Math.cbrt(args.getFirst()));
        });

        FUNCTION_MAP.put("sqrt", args -> {
            if (args.size() != 1) throw ex(args.size(), "sqrt");
            return bounded(Math.sqrt(args.getFirst()));
        });

        FUNCTION_MAP.put("nroot", args -> {
            if (args.size() != 2) throw ex(args.size(), "nroot");
            return bounded(nroot(args.get(0), args.get(1)));
        });

        FUNCTION_MAP.put("min", args -> {
            if (args.isEmpty()) throw ex(0, "min");
            if (args.size() == 1)
                return args.getFirst();

            double min = args.getFirst();

            for (Double value : args)
                if (value < min)
                    min = value;

            return bounded(min);
        });

        FUNCTION_MAP.put("max", args -> {
            if (args.isEmpty()) throw ex(0, "max");
            if (args.size() == 1)
                return args.getFirst();

            double max = args.getFirst();

            for (Double value : args)
                if (value > max)
                    max = value;

            return bounded(max);
        });

        FUNCTION_MAP.put("sq", args -> {
            if (args.size() != 1) throw ex(args.size(), "sq");
            return bounded(args.getFirst() * args.getFirst());
        });

        FUNCTION_MAP.put("cb", args -> {
            if (args.size() != 1) throw ex(args.size(), "cb");
            return bounded(args.getFirst() * args.getFirst() * args.getFirst());
        });

        FUNCTION_MAP.put("arcsin", args -> {
            if (args.size() != 1) throw ex(args.size(), "arcsin");
            return bounded(asin(args.getFirst()));
        });

        FUNCTION_MAP.put("arccos", args -> {
            if (args.size() != 1) throw ex(args.size(), "arccos");
            return bounded(Math.acos(args.getFirst()));
        });

        FUNCTION_MAP.put("arctg", args -> {
            if (args.size() != 1) throw ex(args.size(), "arctg");
            return bounded(Math.atan(args.getFirst()));
        });

        FUNCTION_MAP.put("productlog", args -> {
            // https://www.desy.de/~t00fri/qcdins/texhtml/lambertw/
            if (args.size() != 1) throw ex(args.size(), "productlog");

            final double x = args.getFirst();
            final double m1 = 0.665;
            final double m2 = 0.0195;

            if (x <= 500.0)
                return bounded(m1 * (1 + m2 * Math.log(x + 1)) * Math.log(x + 1) + 0.04);
            else
                return bounded(Math.log(x - 4.0) - (1.0 - 1.0 / Math.log(x)) * Math.log(Math.log(x)));
        });

        FUNCTION_MAP.put("arcctg", args -> {
            if (args.size() != 1) throw ex(args.size(), "arcctg");

            final double arg = args.getFirst();

            return bounded(Math.acos(arg / Math.sqrt(1 + arg * arg)));
        });

        FUNCTION_MAP.put("randr", args -> {
            if (args.size() != 2) throw ex(args.size(), "randr");

            final double left = args.get(0);
            final double right = args.get(1);

            return bounded(left + (right - left) * Math.random());
        });

        FUNCTION_MAP.put("rand", args -> {
            if (args.size() != 1) throw ex(args.size(), "rand");

            final double left = 0;
            final double right = args.getFirst();

            return bounded(left + (right - left) * Math.random());
        });

        FUNCTION_MAP.put("gaussdensity", args -> {
            if (args.size() != 1) throw ex(args.size(), "gaussdensity");
            return bounded(normal(args.getFirst(), 0.0, 1.0));
        });

        FUNCTION_MAP.put("gaussdensityp", args -> {
            if (args.size() != 3) throw ex(args.size(), "gaussdensityp");
            return bounded(normal(args.get(0), args.get(1), args.get(2)));
        });

        FUNCTION_MAP.put("gaussrand", args -> {
            if (args.size() != 2) throw ex(args.size(), "gaussrand");
            double mean = Math.abs(args.getFirst());
            double sd   = Math.abs(args.getLast() + 0.01);

            NormalDistribution distribution = new NormalDistribution(mean, sd);

            return distribution.sample();
        });


        FUNCTION_MAP.put("gamma", args -> {
            if (args.size() != 1) throw ex(args.size(), "gamma");
            return bounded(Gamma.gamma(args.getFirst()));
        });

        FUNCTION_MAP.put("digamma", args -> {
            if (args.size() != 1) throw ex(args.size(), "digamma");
            return bounded(Gamma.digamma(args.getFirst()));
        });

        FUNCTION_MAP.put("trigamma", args -> {
            if (args.size() != 1) throw ex(args.size(), "trigamma");
            return bounded(Gamma.trigamma(args.getFirst()));
        });

        FUNCTION_MAP.put("erf", args -> {
            if (args.size() != 1) throw ex(args.size(), "erf");
            return bounded(org.apache.commons.math3.special.Erf.erf(args.getFirst()));
        });

        FUNCTION_MAP.put("erfinv", args -> {
            if (args.size() != 1) throw ex(args.size(), "erfinv");
            return bounded(org.apache.commons.math3.special.Erf.erfInv(args.getFirst()));
        });

        FUNCTION_MAP.put("si", args -> {
            if (args.size() != 1) throw ex(args.size(), "si");
            return bounded(si(args.getFirst()));
        });

        FUNCTION_MAP.put("ci", args -> {
            if (args.size() != 1) throw ex(args.size(), "ci");
            return bounded(ci(args.getFirst()));
        });

        FUNCTION_MAP.put("li", args -> {
            if (args.size() != 1) throw ex(args.size(), "li");
            return bounded(li(args.getFirst()));
        });

        FUNCTION_MAP.put("ei", args -> {
            if (args.size() != 1) throw ex(args.size(), "ei");
            return bounded(ei(args.getFirst()));
        });

        FUNCTION_MAP.put("digits", args -> {
            if (args.size() != 1) throw ex(args.size(), "digits");
            return bounded(digits(args.getFirst()));
        });
    }

    static {
        SORTED_NAMES.addAll(FUNCTION_MAP.keySet());

        SORTED_NAMES.sort((o1, o2) -> Integer.compare(o2.length(), o1.length()));
    }

    private static double digits(final double x) {
        String r = Precision.format(x);
        double dgs = 0;

        for (int i = r.charAt(0) == '-' ? 1 : 0; i < r.length(); i++) {
            char dg = r.charAt(i);

            dgs += dg == '.' ? 0 : Double.parseDouble(String.valueOf(dg));
        }

        return dgs;
    }

    private static double pow(final double x,
                              final double y) {
        return Math.pow(x, y);
    }

    private static double asin(final double x) {
        return Math.asin(x);
    }

    private static double normal(final double x,
                                 final double mean,
                                 final double sd) {
        if (sd < 0.0)
            return Double.NaN;

        final double m = 1.0 / (sd * Math.sqrt(2.0 * Math.PI));
        final double exp0 = (x - mean) / sd;
        final double exp1 = pow(exp0, 2.0) * (-1.0 / 2.0);

        return m * Math.exp(exp1);
    }

    private static double nroot(final double x,
                                final double n) {
        return ((int)Math.floor(n)) % 2 == 0 ? Math.pow(x, 1.0 / n) : (x >= 0 ? Math.pow(x, 1.0 / n) : -Math.pow(-x, 1.0 / n));
    }

    private static double li(double x) {
        double iterations = 50;

        final double em = 0.577215664901532;
        final double dl = Math.log(Math.abs(Math.log(x)));

        double r = 0;

        for (double n = 1; n < iterations; n += 1) {
            try {
                r += Math.pow(Math.log(x), n) / (n * factorial(n));
            } catch (StackOverflowError e) {
                return NAN;
            }
        }

        return em + dl + r;
    }

    private static double factorial(double x) {
        if (x == 0 || x == 1)
            return 1;
        else
            try {
                return x * factorial(x - 1);
            } catch (StackOverflowError e) {
                return Double.MAX_VALUE;
            }
    }

    private static double ei(final double x) {
        return li(Math.exp(x));
    }

    private static double si(final double x) {
        return CiSi.si(x);
    }

    private static double ci(final double x) {
        return CiSi.ci(x);
    }

    private static class CiSi {

        private final static double
                SU2 = 4.54393409816329991E-2,
                SU4 = 1.15457225751016682E-3,
                SU6 = 1.41018536821330254E-5,
                SU8 = 9.43280809438713025E-8,
               SU10 = 3.53201978997168357E-10,
               SU12 = 7.08240282274875911E-13,
               SU14 = 6.05338212010422477E-16,

                SL2 = 1.01162145739225565E-2,
                SL4 = 4.99175116169755106E-5,
                SL6 = 1.55654986308745614E-7,
                SL8 = 3.28067571055789734E-10,
               SL10 = 4.50490975753865810E-13,
               SL12 = 3.21107051193712168E-16,

                CU2 = 7.51851524438898291E-3,
                CU4 = 1.27528342240267686E-4,
                CU6 = 1.05297363846239184E-6,
                CU8 = 4.68889508144848019E-9,
               CU10 = 1.06480802891189243E-11,
               CU12 = 9.93728488857585407E-15,

                CL2 = 1.15926056891107350E-2,
                CL4 = 6.72126800814254432E-5,
                CL6 = 2.55533277086129636E-7,
                CL8 = 6.97071295760958946E-10,
               CL10 = 1.38536352772778619E-12,
               CL12 = 1.89106054713059759E-15,
               CL14 = 1.39759616731376855E-18;

        private static double s(final double x) {
            double su = 1.0
                    - SU2  * Math.pow(x, 2)
                    + SU4  * Math.pow(x, 4)
                    - SU6  * Math.pow(x, 6)
                    + SU8  * Math.pow(x, 8)
                    - SU10 * Math.pow(x, 10)
                    + SU12 * Math.pow(x, 12)
                    - SU14 * Math.pow(x, 14);

            double sl = 1.0
                    + SL2  * Math.pow(x, 2)
                    + SL4  * Math.pow(x, 4)
                    + SL6  * Math.pow(x, 6)
                    + SL8  * Math.pow(x, 8)
                    + SL10 * Math.pow(x, 10)
                    + SL12 * Math.pow(x, 12);

            return x * su / sl;
        }

        private static double c(final double x) {
            double cu = -0.25
                    + CU2  * Math.pow(x, 2)
                    - CU4  * Math.pow(x, 4)
                    + CU6  * Math.pow(x, 6)
                    - CU8  * Math.pow(x, 8)
                    + CU10 * Math.pow(x, 10)
                    - CU12 * Math.pow(x, 12);

            double cl = 1.0
                    + CL2  * Math.pow(x, 2)
                    + CL4  * Math.pow(x, 4)
                    + CL6  * Math.pow(x, 6)
                    + CL8  * Math.pow(x, 8)
                    + CL10 * Math.pow(x, 10)
                    + CL12 * Math.pow(x, 12)
                    + CL14 * Math.pow(x, 14);

            return 0.5772156649 + Math.log(x) + x * x * (cu / cl);
        }

        private final static double
                FU2 = 744.437068161936700618,
                FU4 = 196396.372895146869801,
                FU6 = 23775031.0125431834034,
                FU8 = 1430734038.21274636888,
               FU10 = 43373623887.0432522765,
               FU12 = 640533830574.022022911,
               FU14 = 4209681805710.76940208,

                FL2 = 746.437068161927678031,
                FL4 = 197865.247031583951450,
                FL6 = 24153567.0165126845144,
                FL8 = 1474789521.92985464958,
               FL10 = 45859511584.7765779830,
               FL12 = 708501308149.515401563,
               FL14 = 5060844645934.75076774,

                GU2 = 813.59520115168615,
                GU4 = 235239.181626478200,
                GU6 = 31255757.0795778731,
                GU8 = 2062975951.46763354,
               GU10 = 68305220542.3625007,
               GU12 = 1090495284503.62786,
               GU14 = 7576645832578.34349,

                GL2 = 819.595201151451564,
                GL4 = 240036.752835578777,
                GL6 = 32602666.1647090822,
                GL8 = 2233555432.78099360,
               GL10 = 78746501734.1829930,
               GL12 = 1398667106964.14565,
               GL14 = 11716472337173.6605;

        private static double f(final double x) {
            double fu = 1.0
                    + FU2 / x
                    + FU4 / Math.pow(x, 2)
                    + FU6 / Math.pow(x, 6)
                    + FU8 / Math.pow(x, 8)
                    + FU10 / Math.pow(x, 10)
                    + FU12 / Math.pow(x, 12)
                    + FU14 / Math.pow(x, 14);

            double fl = 1.0
                    + FL2 / x
                    + FL4 / Math.pow(x, 2)
                    + FL6 / Math.pow(x, 6)
                    + FL8 / Math.pow(x, 8)
                    + FL10 / Math.pow(x, 10)
                    + FL12 / Math.pow(x, 12)
                    + FL14 / Math.pow(x, 14);

            return (1 / x) / (fu / fl);
        }

        private static double g(final double x) {
            double gu = 1
                    + GU2  / x
                    + GU4  / Math.pow(x, 2)
                    + GU6  / Math.pow(x, 6)
                    + GU8  / Math.pow(x, 8)
                    + GU10 / Math.pow(x, 10)
                    + GU12 / Math.pow(x, 12)
                    + GU14 / Math.pow(x, 14);

            double gl = 1
                    + GL2  / x
                    + GL4  / Math.pow(x, 2)
                    + GL6  / Math.pow(x, 6)
                    + GL8  / Math.pow(x, 8)
                    + GL10 / Math.pow(x, 10)
                    + GL12 / Math.pow(x, 12)
                    + GL14 / Math.pow(x, 14);

            return (1 / (x * x)) / (gu / gl);
        }

        private static double si(double x) {
            if (Math.abs(x) >= 4.0)
                if (x > 4.0)
                    return Math.PI / 2 - f(x) * Math.cos(x) - g(x) * Math.sin(x);
                else
                    return -(Math.PI / 2 - f(-x) * Math.cos(x) - g(x) * Math.sin(-x));

            else
                if (x >= 0.0)
                    return s(x);
                else
                    return -s(-x);
        }

        private static double ci(double x) {
            if (x <= 0.0) {
                return NAN;
            } else if (x < 4.0) {
                return c(x);
            } else {
                return f(x) * Math.sin(x) - g(x) * Math.cos(x);
            }
        }

    }

    private static double bounded(final double value) {
        return value > MAX_VALUE && CONSTRAIN ? NAN : value;
    }
}
