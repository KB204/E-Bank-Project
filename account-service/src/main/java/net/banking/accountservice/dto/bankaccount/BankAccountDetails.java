package net.banking.accountservice.dto.bankaccount;

import lombok.Builder;
import net.banking.accountservice.dto.operation.TransactionDTO;

import java.util.List;
@Builder
public record BankAccountDetails(
        String rib,
        List<TransactionDTO> transaction) {}
