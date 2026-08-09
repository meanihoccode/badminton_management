package com.example.java_basic.service;

import com.example.java_basic.dto.MatchResultDTO;
import com.example.java_basic.entity.BadmintonSession;
import com.example.java_basic.entity.User;
import com.example.java_basic.repository.BadmintonSessionRepository;
import com.example.java_basic.repository.GameMatchRepository;
import com.example.java_basic.repository.MatchParticipantRepository;
import com.example.java_basic.repository.TransactionRepository;
import com.example.java_basic.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock
    private GameMatchRepository matchRepository;
    @Mock
    private MatchParticipantRepository participantRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private BadmintonSessionRepository sessionRepository;

    @InjectMocks
    private MatchService matchService;

    private BadmintonSession mockSession;
    private User playerA1, playerA2, playerB1, playerB2;

    @BeforeEach
    void setUp() {
        mockSession = new BadmintonSession();
        mockSession.setId(1L);

        playerA1 = new User(); playerA1.setId(1L); playerA1.setBalance(BigDecimal.ZERO);
        playerA2 = new User(); playerA2.setId(2L); playerA2.setBalance(BigDecimal.ZERO);
        playerB1 = new User(); playerB1.setId(3L); playerB1.setBalance(BigDecimal.ZERO);
        playerB2 = new User(); playerB2.setId(4L); playerB2.setBalance(BigDecimal.ZERO);
    }

    @Test
    void testRecordMatchResult_TeamAWins() {
        // Arrange
        MatchResultDTO dto = new MatchResultDTO();
        dto.setSessionId(1L);
        dto.setTeamAScore(21);
        dto.setTeamBScore(18);
        dto.setPlayerA1Id(1L);
        dto.setPlayerA2Id(2L);
        dto.setPlayerB1Id(3L);
        dto.setPlayerB2Id(4L);

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(mockSession));
        when(userRepository.findById(1L)).thenReturn(Optional.of(playerA1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(playerA2));
        when(userRepository.findById(3L)).thenReturn(Optional.of(playerB1));
        when(userRepository.findById(4L)).thenReturn(Optional.of(playerB2));

        // Act
        matchService.recordMatchResult(dto);

        // Assert
        // A th?ng 5000, B thua 5000
        BigDecimal winAmount = new BigDecimal("5000");
        BigDecimal loseAmount = new BigDecimal("-5000");

        assertEquals(winAmount, playerA1.getBalance());
        assertEquals(winAmount, playerA2.getBalance());
        assertEquals(loseAmount, playerB1.getBalance());
        assertEquals(loseAmount, playerB2.getBalance());

        // Verify save calls (1 match, 4 participants, 4 users, 4 transactions)
        verify(matchRepository, times(1)).save(any());
        verify(participantRepository, times(4)).save(any());
        verify(userRepository, times(4)).save(any());
        verify(transactionRepository, times(4)).save(any());
    }

    @Test
    void testRecordMatchResult_TeamBWins() {
        // Arrange
        MatchResultDTO dto = new MatchResultDTO();
        dto.setSessionId(1L);
        dto.setTeamAScore(15);
        dto.setTeamBScore(21);
        dto.setPlayerA1Id(1L);
        dto.setPlayerA2Id(2L);
        dto.setPlayerB1Id(3L);
        dto.setPlayerB2Id(4L);

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(mockSession));
        when(userRepository.findById(1L)).thenReturn(Optional.of(playerA1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(playerA2));
        when(userRepository.findById(3L)).thenReturn(Optional.of(playerB1));
        when(userRepository.findById(4L)).thenReturn(Optional.of(playerB2));

        // Act
        matchService.recordMatchResult(dto);

        // Assert
        // B th?ng 5000, A thua 5000
        BigDecimal winAmount = new BigDecimal("5000");
        BigDecimal loseAmount = new BigDecimal("-5000");

        assertEquals(loseAmount, playerA1.getBalance());
        assertEquals(loseAmount, playerA2.getBalance());
        assertEquals(winAmount, playerB1.getBalance());
        assertEquals(winAmount, playerB2.getBalance());
    }
}
