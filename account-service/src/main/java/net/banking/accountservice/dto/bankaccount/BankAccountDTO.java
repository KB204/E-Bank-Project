package net.banking.accountservice.dto.bankaccount;


import net.banking.accountservice.enums.AccountStatus;

public record BankAccountDTO(
        String rib,
        Double balance,
        String currency,
        AccountStatus accountStatus,
        String customerIdentity) {}
