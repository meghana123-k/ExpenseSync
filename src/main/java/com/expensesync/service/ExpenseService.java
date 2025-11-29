package com.expensesync.service;

import com.expensesync.dto.CategorySummaryDto;
import com.expensesync.dto.MonthlySummaryDto;
import com.expensesync.entity.Budget;
import com.expensesync.entity.Expense;
import com.expensesync.repository.BudgetRepository;
import com.expensesync.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExpenseService {
    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    public Expense addExpense(Long userId, Double amount, String category, LocalDate date, String note) {
        Expense e = new Expense();
        e.setUserId(userId);
        e.setAmount(amount);
        e.setCategory(category);
        e.setDate(date == null ? LocalDate.now() : date);
        e.setNote(note);
        Expense saved = expenseRepository.save(e);

        // Check budget and return possible alert via caller
        return saved;
    }

    public List<Expense> listExpenses(Long userId) {
        return expenseRepository.findByUserIdOrderByDateDesc(userId);
    }

    public List<CategorySummaryDto> categorySummary(Long userId, YearMonth month) {
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();
        List<Expense> list = expenseRepository.findByUserIdAndDateBetween(userId, start, end);

        Map<String, Double> map = new HashMap<>();
        for (Expense e : list) {
            map.put(e.getCategory(), map.getOrDefault(e.getCategory(), 0.0) + e.getAmount());
        }
        return map.entrySet().stream()
                .map(en -> new CategorySummaryDto(en.getKey(), en.getValue()))
                .collect(Collectors.toList());
    }

    public MonthlySummaryDto monthlyTotal(Long userId, YearMonth month) {
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();
        List<Expense> list = expenseRepository.findByUserIdAndDateBetween(userId, start, end);
        Double total = list.stream().mapToDouble(Expense::getAmount).sum();
        return new MonthlySummaryDto(month.getMonthValue(), month.getYear(), total);
    }

    // Budget methods
    public Budget setBudget(Long userId, Integer month, Integer year, Double limit) {
        Optional<Budget> opt = budgetRepository.findByUserIdAndMonthAndYear(userId, month, year);
        Budget b;
        if (opt.isPresent()) {
            b = opt.get();
            b.setLimitAmount(limit);
        } else {
            b = new Budget();
            b.setUserId(userId);
            b.setMonth(month);
            b.setYear(year);
            b.setLimitAmount(limit);
        }
        return budgetRepository.save(b);
    }

    public Optional<Budget> getBudgetForMonth(Long userId, Integer month, Integer year) {
        return budgetRepository.findByUserIdAndMonthAndYear(userId, month, year);
    }
}
