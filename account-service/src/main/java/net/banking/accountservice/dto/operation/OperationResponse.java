package net.banking.accountservice.dto.operation;

import jakarta.persistence.*;
import net.banking.accountservice.dto.Customer;
import net.banking.accountservice.dto.bankaccount.BankAccountDTO;
import net.banking.accountservice.enums.TransactionType;
import net.banking.accountservice.model.BankAccount;
import net.banking.accountservice.model.CurrentAccount;

import java.time.LocalDateTime;
import java.util.List;

public record OperationResponse(
        Long id,
        String motif,
        String description,
        LocalDateTime createdAt,
        Double amount,
        TransactionType transactionType,
        BankAccountDTO bankAccount,
        List<Customer> customer) {}
