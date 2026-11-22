package com.group9.gym.model;

public class YearlyPlan extends Plan {
    public YearlyPlan() { super(10000); }
    @Override public double calculateFee() { return baseFee; }
    @Override public int getDurationMonths() { return 12; }
}
