package com.example.java_basic.service;

import com.example.java_basic.dto.TransactionResponseDTO;

import java.util.List;

public interface ExcelExportService {
    byte[] exportTransactionsToExcel(List<TransactionResponseDTO> transactions);
}
