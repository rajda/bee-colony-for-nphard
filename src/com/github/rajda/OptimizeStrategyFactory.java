package com.github.rajda;

/**
 * Created by Jacek on 31.01.2016.
 */
public class OptimizeStrategyFactory {
    public enum Type {
        EXCHANGE_TWO_CUSTOMERS,
        EXCHANGE_MIN_PARTITION
    }

    private OptimizeStrategyFactory() {
        // never instantiate, use static factory methods
    }

    public static OptimizeStrategy getStrategyExchangeTwoCustomers() {
        return new ExchangeTwoCustomers();
    }

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
