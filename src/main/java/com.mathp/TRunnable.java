package com.mathp;

import java.util.logging.Level;

public class TRunnable implements Runnable {

    private final double start;
    private final double step;
    private double result;

    public TRunnable(double start, double end, double step) {
        this.start = start;
        this.step = step;
    }

    @Override
    public void run() {
        for (double x = start; x < start + Graph.getComputationRange().getLength() / Graph.getThreads(); x = x + step) {

        }
    }
}
