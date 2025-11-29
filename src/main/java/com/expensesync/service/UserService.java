package com.expensesync.service;

import com.expensesync.entity.AppUser;
import com.expensesync.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AppUser register(String name, String email, String rawPassword) {
        AppUser u = new AppUser();
        u.setName(name);
        u.setEmail(email);
        u.setPassword(encoder.encode(rawPassword));
        return userRepository.save(u);
    }

    public Optional<AppUser> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<AppUser> findById(Long id) { return userRepository.findById(id); }

    public boolean checkPassword(AppUser user, String rawPassword) {
        return encoder.matches(rawPassword, user.getPassword());
    }
}
