package com.group9.gym.model;

public class QuarterlyPlan extends Plan {
    public QuarterlyPlan() { super(2700); }
    @Override public double calculateFee() { return baseFee; }
    @Override public int getDurationMonths() { return 3; }
}
