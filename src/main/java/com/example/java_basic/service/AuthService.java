package com.example.java_basic.service;

import com.example.java_basic.entity.User;
import com.example.java_basic.repository.UserRepository;
import com.example.java_basic.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Transactional
    public void register(Map<String, String> request) {
        String role = request.getOrDefault("role", "MEMBER").toUpperCase();

        User user = User.builder()
                .username(request.get("username"))
                .password(passwordEncoder.encode(request.get("password")))
                .fullName(request.get("fullName"))
                .role(role)
                .racketModel(request.get("racketModel"))
                .balance(BigDecimal.ZERO)
                .build();

        userRepository.save(user);
    }

    public String login(Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        // Xác thực tài khoản
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        // Sinh token
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return jwtService.generateToken(userDetails);
    }
}