package com.github.rajda;

import java.util.ArrayList;

/**
 * Created by Jacek on 02.02.2016.
 */
public interface Problem {
    void createInitialSolution();
    Solution optimize(Solution solution);
//    void optimize();
    Fitness countFitness(int[] solution);

    ProblemInitData getProblemInitData();
    ArrayList<Solution> getSolutionsList();

    /**
     * Put better solution (according to fitness) in place the worse one
     * @param betterSolution
     * @param worseSolution
     */
    void putInPlace(Solution betterSolution, Solution worseSolution);
}
