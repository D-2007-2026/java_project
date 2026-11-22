package com.group9.gym.model;

public class MonthlyPlan extends Plan {
    public MonthlyPlan() { super(1000); }
    @Override public double calculateFee() { return baseFee; }
    @Override public int getDurationMonths() { return 1; }
}
