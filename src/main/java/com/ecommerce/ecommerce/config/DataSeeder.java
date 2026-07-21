package com.ecommerce.ecommerce.config;

import com.ecommerce.ecommerce.model.Role;
import com.ecommerce.ecommerce.model.User;
import com.ecommerce.ecommerce.repository.RoleRepository;
import com.ecommerce.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Check if the admin user already exists to prevent duplicate creation
        if (!userRepository.existsByUsername("admin")) {
            
            // Retrieve the roles created by data.sql
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("ADMIN role not found. Ensure data.sql has run."));
            Role userRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new RuntimeException("USER role not found. Ensure data.sql has run."));

            // Create the Super Admin user
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123")) // Securely hash the password
                    .email("admin@ecommerce.com")
                    .firstName("System")
                    .lastName("Admin")
                    .roles(Set.of(adminRole, userRole)) // Give them both roles
                    .build();

            userRepository.save(admin);
            
            System.out.println("=============================================");
            System.out.println("✅ Default Admin User created successfully!");
            System.out.println("   Username: admin");
            System.out.println("   Password: admin123");
            System.out.println("=============================================");
        }
    }
}
