package com.github.rajda;

import com.github.rajda.idpProblem.IdpFitness;

import java.util.Arrays;

import static com.github.rajda.Helper.random;

public class Solution implements Cloneable {
    private int[] solution;
    private IdpFitness idpFitness;
    private int solutionSize;
    public Solution(int[] solution) {
        this.solution = solution;
        this.solutionSize = solution.length;
    }

    public Solution clone() {
        try {
            Solution cloneSolution = (Solution) super.clone();
            cloneSolution.solution = solution.clone();
            return cloneSolution;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean betterThan(Solution comparedWith) {
        return this.getFitness().getValue() < comparedWith.getFitness().getValue();
    }

    public Fitness getFitness() {
        return idpFitness;
    }

    public void setFitness(Fitness idpFitness) {
        this.idpFitness = (IdpFitness) idpFitness;
    }

    public int getValueAt(int valueId) {
        return solution[valueId];
    }

    public void setValueAt(int valueId, int newValue) {
        solution[valueId] = newValue;
    }

    public int getNumberOfMinPart() {
        return idpFitness.getMinPartitionNumber();
    }

    public int getNumberOfMaxPart() {
        return idpFitness.getMaxPartitionNumber();
    }

    public int getRandomUserFromNotMinPartition() {
        int indexOfUser = random(0, solutionSize - 1);
        while (solution[indexOfUser] == getNumberOfMinPart()) {
            indexOfUser = random(0, solutionSize - 1);
        }
        return indexOfUser;
    }

    public int getRandomUser(int partition) {
        int indexOfUser = random(0, solutionSize - 1);
        while (solution[indexOfUser] != partition) {
            indexOfUser = random(0, solutionSize - 1);
        }
        return indexOfUser;
    }

    @Override
    public String toString() {
        return getFitness().getValue() + ", " + getNumberOfMinPart() + ", " + getNumberOfMaxPart() + " : " + Arrays.toString(solution);
    }
}
