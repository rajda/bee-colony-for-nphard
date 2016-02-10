package com.github.rajda;

public class Fitness {
    private final int value;

    protected Fitness(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
