package com.github.rajda.idpProblem;

import com.github.rajda.ProblemInitData;

/**
 * Created by Jacek on 01.02.2016.
 */
public class IdpProblemInitData implements ProblemInitData {
    private final int customersNumber;
    private final int bandwidth;
    private final int linksNumber;
    private final int lowerLimit;
    private final int upperLimit;

    public IdpProblemInitData(int customersNumber, int linksNumber, int bandwidth, int lowerLimit, int upperLimit) {
        this.customersNumber = customersNumber;
        this.linksNumber = linksNumber;
        this.bandwidth = bandwidth;
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
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
}
