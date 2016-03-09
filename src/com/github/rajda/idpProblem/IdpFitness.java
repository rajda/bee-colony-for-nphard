package com.github.rajda.idpProblem;

import com.github.rajda.Fitness;

/**
 * Quality of IDP solution
 */
public class IdpFitness extends Fitness {
    private final int minPartitionId;
    private final int maxPartitionId;
    private final int admCount;

    public IdpFitness(int totalFitness, int minPartitionId, int maxPartitionId, int admCount) {
        super(totalFitness);
        this.minPartitionId = minPartitionId;
        this.maxPartitionId = maxPartitionId;
        this.admCount = admCount;
    }

    public int getMinPartitionId() {
        return minPartitionId;
    }

    public int getMaxPartitionId() {
        return maxPartitionId;
    }

    @Override
    public String toString() {
        return super.toString() + " " + minPartitionId + " " + maxPartitionId + " " + admCount;
    }
}
