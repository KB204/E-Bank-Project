package net.banking.accountservice.dto.operation;

import net.banking.accountservice.dto.Customer;
import net.banking.accountservice.dto.bankaccount.BankAccountDTO;
import net.banking.accountservice.enums.TransactionType;


import java.time.LocalDateTime;

public record OperationResponse(
        Long id,
        String motif,
        String description,
        LocalDateTime createdAt,
        Double amount,
        TransactionType transactionType,
        BankAccountDTO bankAccount,
        Customer customer) {}
