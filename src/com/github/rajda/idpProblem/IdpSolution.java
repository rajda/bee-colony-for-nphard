package com.github.rajda.idpProblem;

import com.github.rajda.Solution;

import static com.github.rajda.Helper.random;

public class IdpSolution extends Solution {
    public IdpSolution(int[] values) {
        super(values);
    }

    public IdpSolution clone() {
        return (IdpSolution) super.clone();
    }

    public int getNumberOfMinPart() {
        return ((IdpFitness) fitness).getMinPartitionNumber();
    }

    private int getNumberOfMaxPart() {
        return ((IdpFitness) fitness).getMaxPartitionNumber();
    }

    public int getRandomUserFromNotMinPartition() {
        int indexOfUser = random(0, valuesLength - 1);
        while (values[indexOfUser] == getNumberOfMinPart()) {
            indexOfUser = random(0, valuesLength - 1);
        }
        return indexOfUser;
    }

    public int getRandomUser(int partition) {
        int indexOfUser = random(0, valuesLength - 1);
        while (values[indexOfUser] != partition) {
            indexOfUser = random(0, valuesLength - 1);
        }
        return indexOfUser;
    }

    @Override
    public String toString() {
        return getNumberOfMinPart() + ", " + getNumberOfMaxPart() + ", " + super.toString();
    }
}
