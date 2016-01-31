package com.github.rajda;

/**
 * Created by Jacek on 31.01.2016.
 */
public class IdpFitness extends Fitness {
    private int minPartitionNumber;
    private int maxPartitionNumber;
    private int admNumber;

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

    public int getAdmNumber() {
        return admNumber;
    }
}
