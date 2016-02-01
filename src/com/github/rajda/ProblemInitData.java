package com.github.rajda;

/**
 * Created by Jacek on 01.02.2016.
 */
public class ProblemInitData {
    private final int customersNumber;
    private final int bandwidth;
    private final int linksNumber;
    private final int lowerLimit;
    private final int upperLimit;
    private final int iterationsNumber;
    private final int optimizationCyclesNumber;

    public ProblemInitData(int customersNumber, int linksNumber, int bandwidth, int lowerLimit, int upperLimit, int iterationsNumber, int optimizationCyclesNumber) {
        this.customersNumber = customersNumber;
        this.linksNumber = linksNumber;
        this.bandwidth = bandwidth;
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
        this.iterationsNumber = iterationsNumber;
        this.optimizationCyclesNumber = optimizationCyclesNumber;
    }

    public int getCustomersNumber() {
        return customersNumber;
    }

    public int getLinksNumber() {
        return linksNumber;
    }

    public int getBandwidth() {
        return bandwidth;
    }

    public int getLowerLimit() {
        return lowerLimit;
    }

    public int getUpperLimit() {
        return upperLimit;
    }

    public int getIterationsNumber() {
        return iterationsNumber;
    }

    public int getOptimizationCyclesNumber() {
        return optimizationCyclesNumber;
    }
}
