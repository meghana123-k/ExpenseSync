package com.expensesync.controller;

import com.expensesync.config.JwtUtil;
import com.expensesync.dto.AuthRequest;
import com.expensesync.dto.AuthResponse;
import com.expensesync.dto.RegisterRequest;
import com.expensesync.entity.AppUser;
import com.expensesync.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (userService.findByEmail(req.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }
        AppUser u = userService.register(req.getName(), req.getEmail(), req.getPassword());
        String token = jwtUtil.generateToken(u.getEmail());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        var opt = userService.findByEmail(req.getEmail());
        if (opt.isEmpty()) return ResponseEntity.status(401).body("Invalid credentials");
        AppUser u = opt.get();
        if (!userService.checkPassword(u, req.getPassword())) return ResponseEntity.status(401).body("Invalid credentials");
        String token = jwtUtil.generateToken(u.getEmail());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
