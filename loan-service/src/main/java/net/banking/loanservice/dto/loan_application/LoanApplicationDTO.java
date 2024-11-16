package net.banking.loanservice.dto.loan_application;

import net.banking.loanservice.enums.LoanType;

public record LoanApplicationDTO(
        String identifier,
        LoanType loanType,
        Integer loanTerm,
        String customerIdentity) {}
