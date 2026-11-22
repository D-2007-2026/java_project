package com.group9.gym.model;

public abstract class Plan {
    protected final double baseFee;

    public Plan(double baseFee) {
        this.baseFee = baseFee;
    }

    public abstract double calculateFee();
    public abstract int getDurationMonths();
}
