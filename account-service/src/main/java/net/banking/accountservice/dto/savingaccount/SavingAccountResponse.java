package net.banking.accountservice.dto.savingaccount;

import net.banking.accountservice.dto.Customer;
import net.banking.accountservice.enums.AccountStatus;

import java.time.LocalDateTime;
import java.util.List;

public record SavingAccountResponse(
        Long id,
        String rib,
        Double balance,
        String currency,
        String branch,
        LocalDateTime createdAt,
        AccountStatus accountStatus,
        List<Customer> customer,
        String customerIdentity,
        Double interest,
        Double withDrawLimit) {}
