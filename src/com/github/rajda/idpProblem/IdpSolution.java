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

    public int getMinPartitionId() {
        return ((IdpFitness) fitness).getMinPartitionId();
    }

    private int getMaxPartitionId() {
        return ((IdpFitness) fitness).getMaxPartitionId();
    }

    public int getRandomUserFromNotMinPartition() {
        int indexOfUser = random(0, valuesLength - 1);
        while (values[indexOfUser] == getMinPartitionId()) {
            indexOfUser = random(0, valuesLength - 1);
        }
        return indexOfUser;
    }

    public int getRandomUser(int partition) {
        int userId = random(0, valuesLength - 1);
        while (values[userId] != partition) {
            userId = random(0, valuesLength - 1);
        }
        return userId;
    }

    @Override
    public String toString() {
        return getMinPartitionId() + ", " + getMaxPartitionId() + ", " + super.toString();
    }
}
