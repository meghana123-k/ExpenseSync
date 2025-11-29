package com.expensesync.dto;

public class CategorySummaryDto {
    private String category;
    private Double total;

    public CategorySummaryDto(String category, Double total) {
        this.category = category;
        this.total = total;
    }

    public String getCategory() { return category; }
    public Double getTotal() { return total; }
}
