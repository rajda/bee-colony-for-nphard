package com.github.rajda;

import java.util.ArrayList;

/**
 * Created by Jacek on 02.02.2016.
 */
public interface Problem {
    void createInitialSolution();
    Solution optimize(Solution solution);
    Solution doSomething(Solution currentSolution);
    Solution minPartitionOptimize(Solution currentSolution);

    void putInPlace(Solution betterSolution, Solution worseSolution);
    Fitness countFitness(Solution solution);
    ArrayList<Solution> getSolutionsList();
}
