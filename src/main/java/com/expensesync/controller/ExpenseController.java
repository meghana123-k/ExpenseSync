package com.expensesync.controller;

import com.expensesync.dto.ExpenseRequest;
import com.expensesync.dto.CategorySummaryDto;
import com.expensesync.dto.MonthlySummaryDto;
import com.expensesync.entity.Expense;
import com.expensesync.entity.Budget;
import com.expensesync.entity.AppUser;
import com.expensesync.repository.UserRepository;
import com.expensesync.service.ExpenseService;
import com.expensesync.repository.BudgetRepository;
import com.expensesync.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {
    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // Add expense
    @PostMapping("/add")
    public ResponseEntity<?> addExpense(@RequestHeader("Authorization") String authHeader,
                                        @RequestBody ExpenseRequest req) {

        String token = authHeader.substring(7);
        String email = jwtUtil.extractUsername(token);
        Optional<AppUser> opt = userRepository.findByEmail(email);
        if (opt.isEmpty()) return ResponseEntity.status(401).body("Unauthorized");
        AppUser u = opt.get();

        Expense saved = expenseService.addExpense(u.getId(), req.getAmount(), req.getCategory(), req.getDate(), req.getNote());

        // Check budget
        YearMonth ym = YearMonth.from(saved.getDate());
        var monthly = expenseService.monthlyTotal(u.getId(), ym);

        Optional<Budget> bOpt = expenseService.getBudgetForMonth(u.getId(), ym.getMonthValue(), ym.getYear());
        if (bOpt.isPresent()) {
            Budget b = bOpt.get();
            if (monthly.getTotal() > b.getLimitAmount()) {
                return ResponseEntity.ok("Expense saved. ALERT: Budget crossed for " + ym + ". Total: " + monthly.getTotal() + ", Limit: " + b.getLimitAmount());
            }
        }

        return ResponseEntity.ok(saved);
    }

    @GetMapping("/list")
    public ResponseEntity<?> list(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String email = jwtUtil.extractUsername(token);
        Optional<AppUser> opt = userRepository.findByEmail(email);
        if (opt.isEmpty()) return ResponseEntity.status(401).body("Unauthorized");
        AppUser u = opt.get();
        return ResponseEntity.ok(expenseService.listExpenses(u.getId()));
    }

    @GetMapping("/category-summary")
    public ResponseEntity<?> categorySummary(@RequestHeader("Authorization") String authHeader,
                                             @RequestParam(required = false) Integer month,
                                             @RequestParam(required = false) Integer year) {
        String token = authHeader.substring(7);
        String email = jwtUtil.extractUsername(token);
        Optional<AppUser> opt = userRepository.findByEmail(email);
        if (opt.isEmpty()) return ResponseEntity.status(401).body("Unauthorized");
        AppUser u = opt.get();

        YearMonth ym;
        if (month == null || year == null) ym = YearMonth.now();
        else ym = YearMonth.of(year, month);
        List<CategorySummaryDto> list = expenseService.categorySummary(u.getId(), ym);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/monthly-summary")
    public ResponseEntity<?> monthlySummary(@RequestHeader("Authorization") String authHeader,
                                            @RequestParam(required = false) Integer month,
                                            @RequestParam(required = false) Integer year) {
        String token = authHeader.substring(7);
        String email = jwtUtil.extractUsername(token);
        Optional<AppUser> opt = userRepository.findByEmail(email);
        if (opt.isEmpty()) return ResponseEntity.status(401).body("Unauthorized");
        AppUser u = opt.get();

        YearMonth ym;
        if (month == null || year == null) ym = YearMonth.now();
        else ym = YearMonth.of(year, month);
        MonthlySummaryDto dto = expenseService.monthlyTotal(u.getId(), ym);
        return ResponseEntity.ok(dto);
    }
}
