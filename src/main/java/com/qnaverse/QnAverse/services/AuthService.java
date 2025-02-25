package com.qnaverse.QnAverse.services;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.qnaverse.QnAverse.dto.UserDTO;
import com.qnaverse.QnAverse.models.User;
import com.qnaverse.QnAverse.models.User.Role;
import com.qnaverse.QnAverse.repositories.UserRepository;
import com.qnaverse.QnAverse.utils.JwtUtil;

/**
 * Handles authentication-related business logic.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Registers a new user. 
     */
    public ResponseEntity<?> register(UserDTO userDTO) {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            return ResponseEntity.badRequest().body("Username already exists.");
        }

        // By default: role = user
        Role finalRole = Role.user;
        if (userDTO.getRole() != null && userDTO.getRole().equalsIgnoreCase("admin")) {
            finalRole = Role.admin;
        }

        // Create user with hashed password
        User user = new User(userDTO.getUsername(),
                             userDTO.getEmail(),
                             passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(finalRole);  
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully with role: " + finalRole);
    }

    /**
     * Logs in existing user. 
     * If user is admin in DB, they have "admin" authority. 
     * If user is normal user in DB, they have "user" authority.
     */
    public ResponseEntity<?> login(UserDTO userDTO) {
        User user = userRepository.findByUsername(userDTO.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check password
        if (!passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid credentials");
        }

        // Get the role of the user (either user or admin)
        String role = user.getRole().name();  // Extract role as String
        System.out.println("User role: " + role);

        // Generate token including the role
        String token = jwtUtil.generateToken(user.getUsername(), role);

        // Return token in response. 
        return ResponseEntity.ok(token);
    }
}
