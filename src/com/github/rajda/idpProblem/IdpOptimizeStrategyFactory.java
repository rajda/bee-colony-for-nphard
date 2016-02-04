package com.github.rajda.idpProblem;

import com.github.rajda.OptimizeStrategy;

/**
 * Created by Jacek on 31.01.2016.
 */
public class IdpOptimizeStrategyFactory {
    public enum Type {
        EXCHANGE_TWO_CUSTOMERS,
        EXCHANGE_MIN_PARTITION
    }

    private IdpOptimizeStrategyFactory() {
        // never instantiate, use static factory methods
    }

    /**
     * Change name to something like swap food source (only one this kind of method per problem)
     * @return
     */
    public static OptimizeStrategy getStrategyExchangeTwoCustomers() {
        return new ExchangeTwoCustomers();
    }

    /**
     * Extra optimization
     * @return
     */
    public static OptimizeStrategy getStrategyExchangeMinPartition() {
        return new ExchangeMinPartition();
    }

    public static OptimizeStrategy getStrategy(Type type) {
        switch(type) {
            case EXCHANGE_TWO_CUSTOMERS:
                return getStrategyExchangeTwoCustomers();
            case EXCHANGE_MIN_PARTITION:
                return getStrategyExchangeMinPartition();
            default:
                throw new IllegalStateException("Not implemented strategy yet: " + type);
        }
    }
}
