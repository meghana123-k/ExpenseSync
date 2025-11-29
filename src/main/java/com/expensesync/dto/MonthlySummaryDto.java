package com.expensesync.dto;

public class MonthlySummaryDto {
    private Integer month;
    private Integer year;
    private Double total;

    public MonthlySummaryDto(Integer month, Integer year, Double total) {
        this.month = month; this.year = year; this.total = total;
    }

    public Integer getMonth() { return month; }
    public Integer getYear() { return year; }
    public Double getTotal() { return total; }
}
