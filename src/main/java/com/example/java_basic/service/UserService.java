package com.example.java_basic.service;

import com.example.java_basic.dto.UserRequestDTO;
import com.example.java_basic.dto.UserResponseDTO;
import com.example.java_basic.dto.UserUpdateRequestDTO;
import com.example.java_basic.dto.TransactionResponseDTO;
import com.example.java_basic.dto.MatchHistoryResponseDTO;
import com.example.java_basic.dto.UserResponseDTO;
import java.math.BigDecimal;
import java.util.List;

public interface UserService {
    UserResponseDTO createUser(UserRequestDTO dto);
    List<UserResponseDTO> getAllUsers(boolean isAdmin);
    UserResponseDTO updateUser(Long id, UserUpdateRequestDTO dto);
    void deleteUser(Long id, String currentUsername);
    UserResponseDTO payDebt(Long userId, BigDecimal amount);
    // Minh họa Overloading: Cùng tên hàm payDebt nhưng thêm tham số note
    UserResponseDTO payDebt(Long userId, BigDecimal amount, String note);
    List<TransactionResponseDTO> getMyTransactions(String username);
    List<MatchHistoryResponseDTO> getMyMatches(String username);
    UserResponseDTO getMyProfile(String username);
}
