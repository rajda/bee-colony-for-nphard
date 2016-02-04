package com.github.rajda;

/**
 * Created by Jacek on 31.01.2016.
 */
public interface OptimizeStrategy {
    Solution optimize(Problem idpProblem, Solution solution);
}
