package net.banking.accountservice.dto.currentaccount;

import net.banking.accountservice.dto.Customer;
import net.banking.accountservice.enums.AccountStatus;

import java.time.LocalDateTime;
import java.util.List;

public record CurrentAccountResponse(
        Long id,
        String rib,
        Double balance,
        String currency,
        String branch,
        LocalDateTime createdAt,
        AccountStatus accountStatus,
        List<Customer> customer,
        String customerIdentity,
        Double overDraftLimit,
        Double overDraftFees) {}
