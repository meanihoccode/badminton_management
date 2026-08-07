package com.example.java_basic.service;

import com.example.java_basic.dto.MatchResultDTO;
import com.example.java_basic.entity.*;
import com.example.java_basic.exception.ResourceNotFoundException;
import com.example.java_basic.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final GameMatchRepository matchRepository;
    private final MatchParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final BadmintonSessionRepository sessionRepository;

    private static final BigDecimal FEE_PER_MATCH = new BigDecimal("5000");

    @Transactional
    public void recordMatchResult(MatchResultDTO dto) {
        // 1. Lấy thông tin buổi đánh
        BadmintonSession session = sessionRepository.findById(dto.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy buổi đánh"));

        // 2. Lấy thông tin 4 người chơi
        User playerA1 = getUser(dto.getPlayerA1Id());
        User playerA2 = getUser(dto.getPlayerA2Id());
        User playerB1 = getUser(dto.getPlayerB1Id());
        User playerB2 = getUser(dto.getPlayerB2Id());

        // 3. Tính toán hiệu số điểm và số tiền tương ứng
        int pointDiff = dto.getTeamAScore() - dto.getTeamBScore();
        BigDecimal feeTeamA, feeTeamB;
        if (pointDiff > 0) {
             feeTeamA = FEE_PER_MATCH;
             feeTeamB = feeTeamA.negate();
        } else {
             feeTeamB = FEE_PER_MATCH;
             feeTeamA = feeTeamB.negate();
        }

        // 4. Lưu thông tin set đấu (GameMatch)
        GameMatch match = GameMatch.builder()
                .session(session)
                .teamAScore(dto.getTeamAScore())
                .teamBScore(dto.getTeamBScore())
                .build();
        matchRepository.save(match);

        // 5. Cập nhật chi tiết từng người chơi (Participant, Balance, Transaction)
        processPlayerResult(playerA1, match, "A", pointDiff, feeTeamA);
        processPlayerResult(playerA2, match, "A", pointDiff, feeTeamA);
        processPlayerResult(playerB1, match, "B", -pointDiff, feeTeamB);
        processPlayerResult(playerB2, match, "B", -pointDiff, feeTeamB);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thành viên: " + userId));
    }

    private void processPlayerResult(User user, GameMatch match, String team, int pointDiff, BigDecimal fee) {
        // Lưu lịch sử tham gia trận
        MatchParticipant participant = MatchParticipant.builder()
                .match(match)
                .user(user)
                .team(team)
                .pointDifference(pointDiff)
                .feeCalculated(fee)
                .build();
        participantRepository.save(participant);

        // Cập nhật số dư ví
        user.setBalance(user.getBalance().add(fee));
        userRepository.save(user);

        // Ghi vào sổ cái kế toán
        String desc = fee.compareTo(BigDecimal.ZERO) >= 0
                ? "Cộng " + fee + " tiền thắng set"
                : "Trừ " + fee.abs() + " tiền thua set";

        Transaction transaction = Transaction.builder()
                .user(user)
                .amount(fee)
                .transactionType("MATCH_FEE")
                .description(desc)
                .build();
        transactionRepository.save(transaction);
    }
}