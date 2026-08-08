package com.example.java_basic.mapper;

import com.example.java_basic.dto.SessionResponseDTO;
import com.example.java_basic.entity.BadmintonSession;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SessionMapper {
    SessionResponseDTO toDto(BadmintonSession session);
}
