package com.expensesync.controller;

import com.expensesync.entity.AppUser;
import com.expensesync.entity.Budget;
import com.expensesync.repository.UserRepository;
import com.expensesync.config.JwtUtil;
import com.expensesync.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/budget")
public class BudgetController {
    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/set")
    public ResponseEntity<?> setBudget(@RequestHeader("Authorization") String authHeader,
                                       @RequestParam Integer month,
                                       @RequestParam Integer year,
                                       @RequestParam Double limit) {
        String token = authHeader.substring(7);
        String email = jwtUtil.extractUsername(token);
        Optional<AppUser> opt = userRepository.findByEmail(email);
        if (opt.isEmpty()) return ResponseEntity.status(401).body("Unauthorized");
        AppUser u = opt.get();

        Budget b = expenseService.setBudget(u.getId(), month, year, limit);
        return ResponseEntity.ok(b);
    }

    @GetMapping("/get")
    public ResponseEntity<?> getBudget(@RequestHeader("Authorization") String authHeader,
                                       @RequestParam Integer month,
                                       @RequestParam Integer year) {
        String token = authHeader.substring(7);
        String email = jwtUtil.extractUsername(token);
        Optional<AppUser> opt = userRepository.findByEmail(email);
        if (opt.isEmpty()) return ResponseEntity.status(401).body("Unauthorized");
        AppUser u = opt.get();

        var bOpt = expenseService.getBudgetForMonth(u.getId(), month, year);
        if (bOpt.isEmpty()) return ResponseEntity.ok("No budget set for this month");
        return ResponseEntity.ok(bOpt.get());
    }
}
