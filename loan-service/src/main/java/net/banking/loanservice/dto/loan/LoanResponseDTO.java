package net.banking.loanservice.dto.loan;

import net.banking.loanservice.enums.LoanStatus;

public record LoanResponseDTO(
        Double principleAmount,
        Double monthlyInstallment,
        LoanStatus status) {}
