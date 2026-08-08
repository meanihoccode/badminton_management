package com.example.java_basic.service;

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
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final MatchParticipantRepository matchParticipantRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO dto) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username đã tồn tại!");
        }

        User user = User.builder()
                .username(dto.getUsername())
                // 2. Mã hóa mật khẩu lưu vào DB
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .fullName(dto.getFullName())
                .role("MEMBER")
                .racketModel(dto.getRacketModel())
                .balance(java.math.BigDecimal.ZERO)
                .build();

        User savedUser = userRepository.save(user);
        return mapToResponseDTO(savedUser);
    }

    public List<UserResponseDTO> getAllUsers() {
        // Sử dụng Stream API và Lambda Expressions để map list Entity sang DTO
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // Hàm tiện ích chuyển đổi Entity -> DTO
    private UserResponseDTO mapToResponseDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .racketModel(user.getRacketModel())
                .balance(user.getBalance())
                .build();
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
                .transactionType("DEPOSIT")
                .description("Thanh toán tiền công nợ / Nạp quỹ")
                .build();
        transactionRepository.save(tx);

        return mapToResponseDTO(savedUser);
    }

    public List<TransactionResponseDTO> getMyTransactions(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("KhA'ng tAm thy user"));

        return transactionRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(tx -> TransactionResponseDTO.builder()
                        .id(tx.getId())
                        .amount(tx.getAmount())
                        .transactionType(tx.getTransactionType())
                        .description(tx.getDescription())
                        .createdAt(tx.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public List<MatchHistoryResponseDTO> getMyMatches(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("KhA'ng tAm thy user"));

        return matchParticipantRepository.findByUserId(user.getId())
                .stream()
                .map(mp -> MatchHistoryResponseDTO.builder()
                        .matchId(mp.getMatch().getId())
                        .courtName(mp.getMatch().getSession().getCourtName())
                        .sessionDate(mp.getMatch().getSession().getSessionDate())
                        .team(mp.getTeam())
                        .teamAScore(mp.getMatch().getTeamAScore())
                        .teamBScore(mp.getMatch().getTeamBScore())
                        .pointDifference(mp.getPointDifference())
                        .feeCalculated(mp.getFeeCalculated())
                        .build())
                .collect(Collectors.toList());
    }
}