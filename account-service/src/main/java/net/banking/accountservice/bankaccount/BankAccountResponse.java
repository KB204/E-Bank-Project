package net.banking.accountservice.bankaccount;

import java.time.LocalDateTime;
import java.util.List;

record BankAccountResponse(
        Long id,
        String rib,
        Double balance,
        String currency,
        String branch,
        LocalDateTime createdAt,
        AccountStatus accountStatus,
        List<Customer> customer,
        String customerIdentity) {}
