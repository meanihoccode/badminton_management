package com.example.java_basic.service;

import com.example.java_basic.dto.*;
import com.example.java_basic.dto.projection.PlayerStatsProjection;

import java.math.BigDecimal;
import java.util.List;

public interface UserService {
    UserResponseDTO createUser(UserRequestDTO userRequestDTO);
    List<UserResponseDTO> getAllUsers(boolean isAdmin, String keyword);
    PageResponseDTO<UserResponseDTO> getAllUsersPaged(boolean isAdmin, String keyword, int page, int size);
    UserResponseDTO updateUser(Long id, UserUpdateRequestDTO dto);
    void deleteUser(Long id, String currentUsername);
    UserResponseDTO payDebt(Long userId, BigDecimal amount);
    UserResponseDTO payDebt(Long userId, BigDecimal amount, String note);
    List<TransactionResponseDTO> getMyTransactions(String username);
    PageResponseDTO<TransactionResponseDTO> getMyTransactionsPaged(String username, java.time.LocalDate date, String type, int page, int size);
    List<MatchHistoryResponseDTO> getMyMatches(String username);
    PageResponseDTO<MatchHistoryResponseDTO> getMyMatchesPaged(String username, java.time.LocalDate date, int page, int size);
    UserResponseDTO getMyProfile(String username);
    List<PlayerStatsProjection> getTopActivePlayers();
}