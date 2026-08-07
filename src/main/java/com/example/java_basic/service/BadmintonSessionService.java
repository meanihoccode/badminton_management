package com.example.java_basic.service;

import com.example.java_basic.dto.SessionRequestDTO;
import com.example.java_basic.dto.SessionResponseDTO;
import com.example.java_basic.entity.*;
import com.example.java_basic.exception.ResourceNotFoundException;
import com.example.java_basic.repository.BadmintonSessionRepository;
import com.example.java_basic.repository.GameMatchRepository;
import com.example.java_basic.repository.TransactionRepository;
import com.example.java_basic.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BadmintonSessionService {

    private final BadmintonSessionRepository sessionRepository;
    private final GameMatchRepository matchRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public SessionResponseDTO createSession(SessionRequestDTO dto) {
        BadmintonSession session = BadmintonSession.builder()
                .courtName(dto.getCourtName())
                .sessionDate(dto.getSessionDate())
                .status("OPEN")
                .totalCourtFee(BigDecimal.ZERO)
                .shuttlecockFee(BigDecimal.ZERO)
                .build();

        BadmintonSession savedSession = sessionRepository.save(session);
        return mapToResponseDTO(savedSession);
    }

    // Hàm chốt sổ: Tính tổng tiền sân/cầu và chia đều cho những người có tham gia đánh
    @Transactional
    public void closeSession(Long sessionId, BigDecimal totalCourtFee, BigDecimal shuttlecockFee) {
        BadmintonSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy buổi đánh"));

        if ("COMPLETED".equals(session.getStatus())) {
            throw new IllegalStateException("Buổi đánh này đã được chốt sổ rồi!");
        }

        session.setTotalCourtFee(totalCourtFee);
        session.setShuttlecockFee(shuttlecockFee);
        session.setStatus("COMPLETED");

        // 1. Lấy danh sách tất cả các trận đấu trong buổi
        List<GameMatch> matches = matchRepository.findBySessionId(sessionId);

        // 2. Tìm ra danh sách những người chơi duy nhất (Unique) có tham gia buổi hôm nay
        Set<User> uniquePlayers = matches.stream()
                .flatMap(match -> match.getParticipants().stream())
                .map(MatchParticipant::getUser)
                .collect(Collectors.toSet());

        if (!uniquePlayers.isEmpty()) {
            // 3. Tính tiền chia đều (Tiền sân + Tiền cầu) / Số người
            BigDecimal totalFee = totalCourtFee.add(shuttlecockFee);
            BigDecimal feePerPlayer = totalFee.divide(new BigDecimal(uniquePlayers.size()), 2, RoundingMode.HALF_UP);

            // 4. Trừ tiền từng người và ghi lịch sử giao dịch
            for (User player : uniquePlayers) {
                player.setBalance(player.getBalance().subtract(feePerPlayer));
                userRepository.save(player);

                Transaction tx = Transaction.builder()
                        .user(player)
                        .amount(feePerPlayer.negate()) // Ghi âm tiền
                        .transactionType("COURT_FEE")
                        .description("Trừ tiền chia sẻ sân và cầu ngày " + session.getSessionDate())
                        .build();
                transactionRepository.save(tx);
            }
        }

        sessionRepository.save(session);
    }

    private SessionResponseDTO mapToResponseDTO(BadmintonSession session) {
        return SessionResponseDTO.builder()
                .id(session.getId())
                .courtName(session.getCourtName())
                .sessionDate(session.getSessionDate())
                .totalCourtFee(session.getTotalCourtFee())
                .shuttlecockFee(session.getShuttlecockFee())
                .status(session.getStatus())
                .build();
    }
}