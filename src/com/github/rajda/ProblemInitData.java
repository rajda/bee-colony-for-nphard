package com.github.rajda;

/**
 * Created by Jacek on 02.02.2016.
 */
public abstract class ProblemInitData {
    private final int iterationsNumber;
    private final int optimizationCyclesNumber;

    public ProblemInitData(int iterationsNumber, int optimizationCyclesNumber) {
        this.iterationsNumber = iterationsNumber;
        this.optimizationCyclesNumber = optimizationCyclesNumber;
    }

    public int getIterationsNumber() {
        return iterationsNumber;
    }

    public int getOptimizationCyclesNumber() {
        return optimizationCyclesNumber;
    }
}
