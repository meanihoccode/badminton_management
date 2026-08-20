package com.example.java_basic.service.impl;
import com.example.java_basic.service.*;

import com.example.java_basic.dto.UserRequestDTO;
import com.example.java_basic.dto.UserResponseDTO;
import com.example.java_basic.dto.PageResponseDTO;
import com.example.java_basic.entity.Transaction;
import com.example.java_basic.entity.User;
import com.example.java_basic.exception.ResourceNotFoundException;
import com.example.java_basic.repository.TransactionRepository;
import com.example.java_basic.repository.UserRepository;
import com.example.java_basic.component.InvoiceGenerator;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final InvoiceGenerator invoiceGenerator;
    private final TransactionMapper transactionMapper;
    private final MatchHistoryMapper matchHistoryMapper;
    private final MessageSource messageSource;

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO dto) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            String msg = messageSource.getMessage("error.username.exists", null, LocaleContextHolder.getLocale());
            throw new IllegalArgumentException(msg);
        }

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("err.auth.email_exists");
        }

        User user = User.builder()
                .username(dto.getUsername())
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
            throw new IllegalStateException("err.user.self_delete");
        }

        userRepository.deleteById(id);
    }

    @Transactional
    public UserResponseDTO payDebt(Long userId, BigDecimal amount) {
        return payDebt(userId, amount, "Thanh toán tiền công nợ / Nạp quỹ");
    }

    @Transactional
    public UserResponseDTO payDebt(Long userId, BigDecimal amount, String note) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thành viên có ID: " + userId));

        user.setBalance(user.getBalance().add(amount));
        User savedUser = userRepository.save(user);

        Transaction tx = Transaction.builder()
                .user(savedUser)
                .amount(amount)
                .transactionType(TransactionType.DEPOSIT)
                .receipt(invoiceGenerator.generateReceipt(savedUser.getFullName(), amount, note))
                .build();
        transactionRepository.save(tx);

        UserResponseDTO dto = userMapper.toDto(savedUser);
        dto.setLatestReceipt(tx.getReceipt());
        return dto;
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
    public PageResponseDTO<UserResponseDTO> getAllUsersPaged(boolean isAdmin, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findAll(pageable);
        List<UserResponseDTO> content = userPage.getContent().stream().map(user -> {
            UserResponseDTO dto = userMapper.toDto(user);
            if (!isAdmin) {
                dto.setBalance(null);
            }
            return dto;
        }).collect(Collectors.toList());
        
        return PageResponseDTO.<UserResponseDTO>builder()
                .content(content)
                .currentPage(userPage.getNumber())
                .totalPages(userPage.getTotalPages())
                .totalElements(userPage.getTotalElements())
                .build();
    }

    public PageResponseDTO<TransactionResponseDTO> getMyTransactionsPaged(String username, int page, int size) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy User"));

        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> txPage = transactionRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable);
        
        List<TransactionResponseDTO> content = txPage.getContent().stream()
                .map(transactionMapper::toDto)
                .collect(Collectors.toList());
                
        return PageResponseDTO.<TransactionResponseDTO>builder()
                .content(content)
                .currentPage(txPage.getNumber())
                .totalPages(txPage.getTotalPages())
                .totalElements(txPage.getTotalElements())
                .build();
    }

    public PageResponseDTO<MatchHistoryResponseDTO> getMyMatchesPaged(String username, int page, int size) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy User"));

        Pageable pageable = PageRequest.of(page, size);
        Page<com.example.java_basic.entity.MatchParticipant> matchPage = matchParticipantRepository.findByUserId(user.getId(), pageable);
        
        List<MatchHistoryResponseDTO> content = matchPage.getContent().stream()
                .map(matchHistoryMapper::toDto)
                .collect(Collectors.toList());
                
        return PageResponseDTO.<MatchHistoryResponseDTO>builder()
                .content(content)
                .currentPage(matchPage.getNumber())
                .totalPages(matchPage.getTotalPages())
                .totalElements(matchPage.getTotalElements())
                .build();
    }
}