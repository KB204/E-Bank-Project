package net.banking.accountservice.dto.savingaccount;

import net.banking.accountservice.dto.Customer;
import net.banking.accountservice.enums.AccountStatus;

import java.time.LocalDateTime;

public record SavingAccountResponse(
        Long id,
        String rib,
        Double balance,
        String currency,
        String branch,
        LocalDateTime createdAt,
        AccountStatus accountStatus,
        Customer customer,
        String customerIdentity,
        String customerEmail,
        Double interest,
        Double withDrawLimit) {}
