package com.group9.gym.model;

import java.time.LocalDate;

public class Payment {
    private final int memberId;
    private final double amount;
    private final LocalDate paymentDate;
    private final String note;

    public Payment(int memberId, double amount, LocalDate paymentDate, String note) {
        this.memberId = memberId; this.amount = amount; this.paymentDate = paymentDate; this.note = note;
    }
    public int getMemberId() { return memberId; }
    public double getAmount() { return amount; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public String getNote() { return note; }
}
