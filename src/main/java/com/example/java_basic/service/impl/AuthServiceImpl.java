package com.example.java_basic.service.impl;

import com.example.java_basic.dto.AuthResponseDTO;
import com.example.java_basic.dto.LoginRequestDTO;
import com.example.java_basic.dto.RegisterRequestDTO;
import com.example.java_basic.entity.User;
import com.example.java_basic.repository.UserRepository;
import com.example.java_basic.security.JwtService;
import com.example.java_basic.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import com.example.java_basic.enums.Role;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final MessageSource messageSource;

    @Override
    @Transactional
    public void register(RegisterRequestDTO request) {
        String username = request.getUsername();
        if (userRepository.findByUsername(username).isPresent()) {
            String msg = messageSource.getMessage("error.username.exists", null, LocaleContextHolder.getLocale());
            throw new IllegalArgumentException(msg);
        }

        String roleStr = request.getRole() != null ? request.getRole().toUpperCase() : "MEMBER";
        Role roleEnum;
        try {
            roleEnum = Role.valueOf(roleStr);
        } catch (IllegalArgumentException e) {
            roleEnum = Role.MEMBER;
        }

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .role(roleEnum)
                .racketModel(request.getRacketModel())
                .balance(BigDecimal.ZERO)
                .build();

        userRepository.save(user);
    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO request) {
        String username = request.getUsername();
        String password = request.getPassword();

        // Xác thực tài khoản
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        // Sinh token
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String token = jwtService.generateToken(userDetails);
        
        String role = userDetails.getAuthorities().iterator().next().getAuthority(); // VD: ROLE_ADMIN
        
        return AuthResponseDTO.builder()
                .token(token)
                .username(userDetails.getUsername())
                .role(role)
                .build();
    }
}

