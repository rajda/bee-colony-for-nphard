package com.github.rajda;

import java.util.ArrayList;

/**
 * Created by Jacek on 02.02.2016.
 */
public interface Problem {
    void createInitialSolutions();
    void optimize();
    Fitness countFitness(int[] solution);

    ProblemInitData getProblemInitData();
    ArrayList<Solution> getSolutionsList();
}
