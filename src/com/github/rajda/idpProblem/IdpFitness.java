package com.github.rajda.idpProblem;

import com.github.rajda.Fitness;

/**
 * Quality of IDP solution
 */
public class IdpFitness extends Fitness {
    private final int minPartitionNumber;
    private final int maxPartitionNumber;
    private final int admNumber;

    public IdpFitness(int totalFitness, int minPartitionNumber, int maxPartitionNumber, int admNumber) {
        super(totalFitness);
        this.minPartitionNumber = minPartitionNumber;
        this.maxPartitionNumber = maxPartitionNumber;
        this.admNumber = admNumber;
    }

    public int getMinPartitionNumber() {
        return minPartitionNumber;
    }

    public int getMaxPartitionNumber() {
        return maxPartitionNumber;
    }

    @Override
    public String toString() {
        return super.toString() + " " + minPartitionNumber + " " + maxPartitionNumber + " " + admNumber;
    }
}
