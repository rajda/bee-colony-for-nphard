package com.github.rajda.idpProblem;

import com.github.rajda.ProblemInitData;

public class IdpProblemInitData implements ProblemInitData {
    private final int customerCount;
    private final int bandwidth;
    private final int linkCount;
    private final int lowerLimit;
    private final int upperLimit;

    public IdpProblemInitData(int customerCount, int linkCount, int bandwidth, int lowerLimit, int upperLimit) {
        this.customerCount = customerCount;
        this.linkCount = linkCount;
        this.bandwidth = bandwidth;
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
    }

    public int getCustomerCount() {
        return customerCount;
    }

    public int getLinkCount() {
        return linkCount;
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

    @Override
    public String toString() {
        return "CustomerCount: " + customerCount + ", linkCount: " + linkCount + ", bandwidth: " + bandwidth + ", lowerLimit: " + lowerLimit + ", upperLimit: " + upperLimit;
    }
}
