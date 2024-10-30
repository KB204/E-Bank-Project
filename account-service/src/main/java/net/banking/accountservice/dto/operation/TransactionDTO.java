package net.banking.accountservice.dto.operation;

import net.banking.accountservice.enums.TransactionType;

import java.time.LocalDateTime;

public record TransactionDTO(
        String motif,
        String description,
        LocalDateTime createdAt,
        Double amount,
        TransactionType transactionType) {}
