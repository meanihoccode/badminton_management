package com.example.java_basic.service;

import com.example.java_basic.entity.BadmintonSession;
import com.example.java_basic.entity.GameMatch;
import com.example.java_basic.entity.MatchParticipant;
import com.example.java_basic.entity.User;
import com.example.java_basic.repository.BadmintonSessionRepository;
import com.example.java_basic.repository.GameMatchRepository;
import com.example.java_basic.repository.TransactionRepository;
import com.example.java_basic.repository.UserRepository;
import com.example.java_basic.mapper.SessionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BadmintonSessionServiceTest {

    @Mock
    private BadmintonSessionRepository sessionRepository;
    @Mock
    private GameMatchRepository matchRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private SessionMapper sessionMapper;

    @InjectMocks
    private BadmintonSessionService sessionService;

    private BadmintonSession mockSession;

    @BeforeEach
    void setUp() {
        mockSession = new BadmintonSession();
        mockSession.setId(1L);
        mockSession.setStatus("OPEN");
    }

    @Test
    void testCloseSession_Success() {
        // Arrange
        BigDecimal courtFee = new BigDecimal("100000");
        BigDecimal shuttlecockFee = new BigDecimal("20000");
        
        // 4 ng?i ch?i
        User u1 = new User(); u1.setId(1L); u1.setBalance(BigDecimal.ZERO);
        User u2 = new User(); u2.setId(2L); u2.setBalance(BigDecimal.ZERO);
        User u3 = new User(); u3.setId(3L); u3.setBalance(BigDecimal.ZERO);
        User u4 = new User(); u4.setId(4L); u4.setBalance(BigDecimal.ZERO);

        GameMatch match1 = new GameMatch();
        MatchParticipant p1 = new MatchParticipant(); p1.setUser(u1);
        MatchParticipant p2 = new MatchParticipant(); p2.setUser(u2);
        MatchParticipant p3 = new MatchParticipant(); p3.setUser(u3);
        MatchParticipant p4 = new MatchParticipant(); p4.setUser(u4);
        match1.setParticipants(List.of(p1, p2, p3, p4));

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(mockSession));
        when(matchRepository.findBySessionId(1L)).thenReturn(List.of(match1));

        // Act
        sessionService.closeSession(1L, courtFee, shuttlecockFee);

        // Assert
        // T?ng ti?n = 120,000 / 4 ng?i = 30,000 / ng?i -> Tr? 30,000
        BigDecimal expectedFee = new BigDecimal("-30000.00");
        
        assertEquals(expectedFee, u1.getBalance());
        assertEquals(expectedFee, u2.getBalance());
        assertEquals(expectedFee, u3.getBalance());
        assertEquals(expectedFee, u4.getBalance());
        assertEquals("COMPLETED", mockSession.getStatus());

        verify(userRepository, times(4)).save(any());
        verify(transactionRepository, times(4)).save(any());
        verify(sessionRepository, times(1)).save(mockSession);
    }

    @Test
    void testCloseSession_AlreadyCompleted_ThrowsException() {
        // Arrange
        mockSession.setStatus("COMPLETED");
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(mockSession));

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            sessionService.closeSession(1L, BigDecimal.ZERO, BigDecimal.ZERO);
        });

        assertEquals("Buổi đánh này đã được chốt sổ rồi!", exception.getMessage());
        verify(matchRepository, never()).findBySessionId(any());
    }
}
