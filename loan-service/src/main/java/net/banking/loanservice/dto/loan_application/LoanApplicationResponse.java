package net.banking.loanservice.dto.loan_application;

import lombok.Builder;
import net.banking.loanservice.dto.external_services.Customer;
import net.banking.loanservice.enums.ApplicationStatus;
import net.banking.loanservice.enums.LoanType;

import java.time.LocalDate;

@Builder
public record LoanApplicationResponse(
        Long id,
        String identifier,
        LoanType loanType,
        Integer loanTerm,
        Double requestedAmount,
        Double interest,
        ApplicationStatus status,
        LocalDate createdAt,
        LocalDate updateAt,
        String customerIdentity,
        Customer customer) {}
