package ru.utils;

public class Measure {

    private static final long NANOS_TO_MILLIS = 1_000_000L;

    public interface Method {
        void execute();
    }

    private Measure() throws InstantiationException {
        throw new InstantiationException();
    }

    public static long measure(Method method) {
        long start    = System.nanoTime();
        method.execute();
        long end      = System.nanoTime();

        long duration;

        try {
            duration = Math.subtractExact(end, start);
        } catch (ArithmeticException e) {
            duration = Long.MAX_VALUE;
        }

        System.out.println("Method[1] --- duration = " + duration + " ns / " + duration / NANOS_TO_MILLIS + " ms");

        return duration;
    }

    public static long[] measure(final Method... methods) {
        long[] durations = new long[methods.length];

        for (int i = 0; i < methods.length; i++) {
            long start = System.nanoTime();
            methods[i].execute();
            long end = System.nanoTime();

            try {
                durations[i] = Math.subtractExact(end, start);
            } catch (ArithmeticException e) {
                durations[i] = Long.MAX_VALUE;
            }

            System.out.println("Method[" + i + "] --- duration = " + durations[i] + " ns / " + durations[i] / NANOS_TO_MILLIS + " ms");
        }

        return durations;
    }

    public static long measure(final long repeat,
                               final Method method) {
        long start = System.nanoTime();

        for (long i = 0; i < repeat; i++) {
            method.execute();
        }

        long end = System.nanoTime();
        long duration;

        try {
            duration = Math.subtractExact(end, start);
        } catch (ArithmeticException e) {
            duration = Long.MAX_VALUE;
        }

        System.out.println("Method[1] --- duration = " + duration + " ns / " + duration / NANOS_TO_MILLIS + " ms");

        return duration;
    }

    public static long[] measure(final long repeat,
                                 final Method... methods) {
        long[] durations = new long[methods.length];

        for (int i = 0; i < methods.length; i++) {
            long start = System.nanoTime();

            for (long l = 0; l < repeat; l++) {
                methods[i].execute();
            }

            long end = System.nanoTime();

            try {
                durations[i] = Math.subtractExact(end, start);
            } catch (ArithmeticException e) {
                durations[i] = Long.MAX_VALUE;
            }

            System.out.println("Method[" + i + "] --- duration = " + durations[i] + " ns / " + durations[i] / NANOS_TO_MILLIS + " ms");
        }

        return durations;
    }


    public static double error(double value,
                               double expected) {

        return Math.abs(value - expected) / expected * 100.0;
    }
}
