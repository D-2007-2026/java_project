package com.group9.gym.model;

import java.time.LocalDate;

public class Member implements Renewable {
    private int id;
    private final String name, phone, email, plan;
    private LocalDate startDate, endDate;

    public Member(String name, String phone, String email, String plan,
                  LocalDate startDate, LocalDate endDate) {
        this.name = name; this.phone = phone; this.email = email; this.plan = plan;
        this.startDate = startDate; this.endDate = endDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getPlan() { return plan; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }

    @Override public void renew() {
        LocalDate base = endDate.isBefore(LocalDate.now()) ? LocalDate.now() : endDate;
        int months = switch (plan) {
            case "Quarterly" -> 3;
            case "Yearly" -> 12;
            default -> 1;
        };
        startDate = base;
        endDate = base.plusMonths(months);
    }
}
