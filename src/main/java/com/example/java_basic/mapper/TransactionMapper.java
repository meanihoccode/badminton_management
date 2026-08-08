package com.example.java_basic.mapper;

import com.example.java_basic.dto.TransactionResponseDTO;
import com.example.java_basic.entity.Transaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    TransactionResponseDTO toDto(Transaction transaction);
}
