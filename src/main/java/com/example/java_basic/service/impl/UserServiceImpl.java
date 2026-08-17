package com.example.java_basic.service.impl;
import com.example.java_basic.service.*;

import com.example.java_basic.dto.UserRequestDTO;
import com.example.java_basic.dto.UserResponseDTO;
import com.example.java_basic.entity.Transaction;
import com.example.java_basic.entity.User;
import com.example.java_basic.exception.ResourceNotFoundException;
import com.example.java_basic.repository.TransactionRepository;
import com.example.java_basic.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import com.example.java_basic.repository.MatchParticipantRepository;
import com.example.java_basic.dto.TransactionResponseDTO;
import com.example.java_basic.dto.MatchHistoryResponseDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import com.example.java_basic.enums.TransactionType;
import com.example.java_basic.enums.Role;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.java_basic.mapper.UserMapper;
import com.example.java_basic.mapper.TransactionMapper;
import com.example.java_basic.mapper.MatchHistoryMapper;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final MatchParticipantRepository matchParticipantRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final TransactionMapper transactionMapper;
    private final MatchHistoryMapper matchHistoryMapper;
    private final MessageSource messageSource;

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO dto) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            String msg = messageSource.getMessage("error.username.exists", null, LocaleContextHolder.getLocale());
            throw new IllegalArgumentException(msg);
        }

        User user = User.builder()
                .username(dto.getUsername())
                // 2. Mã hóa mật khẩu lưu vào DB
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .fullName(dto.getFullName())
                .role(dto.getRole() != null ? dto.getRole() : Role.MEMBER)
                .racketModel(dto.getRacketModel())
                .balance(java.math.BigDecimal.ZERO)
                .build();

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    public List<UserResponseDTO> getAllUsers(boolean isAdmin) {
        return userRepository.findAll().stream().map(user -> {
            UserResponseDTO dto = userMapper.toDto(user);
            if (!isAdmin) {
                dto.setBalance(null);
            }
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public UserResponseDTO updateUser(Long id, com.example.java_basic.dto.UserUpdateRequestDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    String msg = messageSource.getMessage("error.user.not.found", new Object[]{id}, LocaleContextHolder.getLocale());
                    return new ResourceNotFoundException(msg);
                });
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setRacketModel(dto.getRacketModel());
        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id, String currentUsername) {
        User userToDelete = userRepository.findById(id)
                .orElseThrow(() -> {
                    String msg = messageSource.getMessage("error.user.not.found", new Object[]{id}, LocaleContextHolder.getLocale());
                    return new ResourceNotFoundException(msg);
                });
        
        if (userToDelete.getUsername().equals(currentUsername)) {
            throw new IllegalStateException("Hành động bị chặn: Bạn không thể tự xóa tài khoản của chính mình!");
        }

        userRepository.deleteById(id);
    }

    @Transactional
    public UserResponseDTO payDebt(Long userId, BigDecimal amount) {
        // 1. Tìm người dùng
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thành viên có ID: " + userId));

        // 2. Cộng tiền thanh toán vào ví hiện tại
        user.setBalance(user.getBalance().add(amount));
        User savedUser = userRepository.save(user);

        // 3. Ghi lại lịch sử nạp tiền/xóa nợ
        Transaction tx = Transaction.builder()
                .user(savedUser)
                .amount(amount)
                .transactionType(TransactionType.DEPOSIT)
                .description("Thanh toán tiền công nợ / Nạp quỹ")
                .build();
        transactionRepository.save(tx);

        return userMapper.toDto(savedUser);
    }

    public List<TransactionResponseDTO> getMyTransactions(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy User"));

        return transactionRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(transactionMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<MatchHistoryResponseDTO> getMyMatches(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy User"));

        return matchParticipantRepository.findByUserId(user.getId())
                .stream()
                .map(matchHistoryMapper::toDto)
                .collect(Collectors.toList());
    }
    @Override
    public UserResponseDTO getMyProfile(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return userMapper.toDto(user);
    }
}
