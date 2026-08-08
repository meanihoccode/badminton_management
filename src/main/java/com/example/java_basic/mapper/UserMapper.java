package com.example.java_basic.mapper;

import com.example.java_basic.dto.UserResponseDTO;
import com.example.java_basic.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDTO toDto(User user);
}
