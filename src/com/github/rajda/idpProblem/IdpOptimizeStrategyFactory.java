package com.github.rajda.idpProblem;

import com.github.rajda.OptimizeStrategy;

public class IdpOptimizeStrategyFactory {
    public enum Type {
        EXCHANGE_TWO_CUSTOMERS,
        EXCHANGE_MIN_PARTITION,
        EXCHANGE_ACCORDING_PROB
    }

    private IdpOptimizeStrategyFactory() {
        // never instantiate, use static factory methods
    }

    /**
     * Change name to something like swap food source (only one this kind of method per problem)
     * @return
     */
    private static OptimizeStrategy getStrategyExchangeTwoCustomers() {
        return new ExchangeTwoCustomers();
    }

    /**
     * local search
     * @return
     */
    private static OptimizeStrategy getStrategyExchangeMinPartition() {
        return new ExchangeMinPartition();
    }

    /**
     * local search
     * @return
     */
    private static OptimizeStrategy getStrategyExchangeAccordingProb() {
        return new ExchangeAccordingProb();
    }

    public static OptimizeStrategy getStrategy(Type type) {
        switch(type) {
            case EXCHANGE_TWO_CUSTOMERS:
                return getStrategyExchangeTwoCustomers();
            case EXCHANGE_MIN_PARTITION:
                return getStrategyExchangeMinPartition();
            case EXCHANGE_ACCORDING_PROB:
                return getStrategyExchangeAccordingProb();
            default:
                throw new IllegalStateException("Not implemented strategy yet: " + type);
        }
    }
}
