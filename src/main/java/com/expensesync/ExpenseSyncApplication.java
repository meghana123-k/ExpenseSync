package com.expensesync;

import com.expensesync.entity.AppUser;
import com.expensesync.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class ExpenseSyncApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExpenseSyncApplication.class, args);
    }

    // create a sample user at startup (email: user@ex.com, pass: password)
    @Bean
    CommandLineRunner init(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByEmail("user@ex.com").isEmpty()) {
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                AppUser u = new AppUser();
                u.setName("Demo User");
                u.setEmail("user@ex.com");
                u.setPassword(encoder.encode("password"));
                userRepository.save(u);
                System.out.println("Created demo user user@ex.com / password");
            }
        };
    }
}
