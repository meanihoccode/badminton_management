package com.example.java_basic.service.impl;

import com.example.java_basic.dto.*;
import com.example.java_basic.entity.User;
import com.example.java_basic.repository.UserRepository;
import com.example.java_basic.security.JwtService;
import com.example.java_basic.service.AuthService;
import com.example.java_basic.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;
import com.example.java_basic.enums.Role;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final EmailService emailService;
    private final CacheManager cacheManager;

    @Override
    @Transactional
    public void register(RegisterRequestDTO request) {
        saveUserToDb(request);
    }

    @Override
    public void initiateRegistration(RegisterRequestDTO request) {
        // Validate is already done by @Valid in Controller
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username đã tồn tại");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }

        // Generate 6-digit OTP
        String otpCode = String.format("%06d", new Random().nextInt(999999));
        
        OtpSessionData sessionData = OtpSessionData.builder()
                .userData(request)
                .otpCode(otpCode)
                .expiryTime(LocalDateTime.now().plusMinutes(5))
                .build();

        // Save to cache
        Cache cache = cacheManager.getCache("otpCache");
        if (cache != null) {
            cache.put(request.getEmail(), sessionData);
        }

        // Send Email via JMS
        emailService.sendOtpEmail(request.getEmail(), request.getFullName(), otpCode);
        log.info("Da khoi tao OTP dang ky cho email: {}", request.getEmail());
    }

    @Override
    @Transactional
    public void verifyRegistration(OtpVerifyRequestDTO request) {
        Cache cache = cacheManager.getCache("otpCache");
        if (cache == null) {
            throw new IllegalArgumentException("Hệ thống cache đang lỗi");
        }

        OtpSessionData sessionData = cache.get(request.getEmail(), OtpSessionData.class);
        if (sessionData == null) {
            throw new IllegalArgumentException("Mã OTP đã hết hạn hoặc email không chính xác");
        }

        if (!sessionData.getOtpCode().equals(request.getOtp())) {
            throw new IllegalArgumentException("Mã OTP không hợp lệ");
        }

        if (sessionData.getExpiryTime().isBefore(LocalDateTime.now())) {
            cache.evict(request.getEmail());
            throw new IllegalArgumentException("Mã OTP đã hết hạn");
        }

        // All good -> Save user
        saveUserToDb(sessionData.getUserData());
        cache.evict(request.getEmail()); // Xóa cache sau khi dùng
        log.info("Xac thuc OTP thanh cong, da tao user cho email: {}", request.getEmail());
    }

    private void saveUserToDb(RegisterRequestDTO request) {
        String roleStr = request.getRole() != null ? request.getRole().toUpperCase() : "MEMBER";
        Role roleEnum;
        try {
            roleEnum = Role.valueOf(roleStr);
        } catch (IllegalArgumentException e) {
            roleEnum = Role.MEMBER;
        }

        User user = User.builder()
                .username(request.getUsername())
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
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtService.generateToken(userDetails);
        String role = userDetails.getAuthorities().iterator().next().getAuthority();
        User userEntity = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        return AuthResponseDTO.builder()
                .token(token)
                .id(userEntity.getId())
                .username(userDetails.getUsername())
                .role(role)
                .build();
    }
}