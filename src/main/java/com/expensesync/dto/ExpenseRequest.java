package com.expensesync.dto;

import java.time.LocalDate;

public class ExpenseRequest {
    private Double amount;
    private String category;
    private LocalDate date; // optional - if null use today
    private String note;

    // getters & setters
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
