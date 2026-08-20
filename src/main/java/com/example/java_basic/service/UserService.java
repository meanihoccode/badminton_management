package com.example.java_basic.service;

import com.example.java_basic.dto.*;
import java.math.BigDecimal;
import java.util.List;

public interface UserService {
    UserResponseDTO createUser(UserRequestDTO userRequestDTO);
    List<UserResponseDTO> getAllUsers(boolean isAdmin);
    PageResponseDTO<UserResponseDTO> getAllUsersPaged(boolean isAdmin, int page, int size);
    UserResponseDTO updateUser(Long id, UserUpdateRequestDTO dto);
    void deleteUser(Long id, String currentUsername);
    UserResponseDTO payDebt(Long userId, BigDecimal amount);
    UserResponseDTO payDebt(Long userId, BigDecimal amount, String note);
    List<TransactionResponseDTO> getMyTransactions(String username);
    PageResponseDTO<TransactionResponseDTO> getMyTransactionsPaged(String username, int page, int size);
    List<MatchHistoryResponseDTO> getMyMatches(String username);
    PageResponseDTO<MatchHistoryResponseDTO> getMyMatchesPaged(String username, int page, int size);
    UserResponseDTO getMyProfile(String username);
}