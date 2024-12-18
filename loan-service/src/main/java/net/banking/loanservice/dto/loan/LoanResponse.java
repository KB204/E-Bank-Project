package net.banking.loanservice.dto.loan;

import net.banking.loanservice.dto.external_services.Customer;
import net.banking.loanservice.dto.loan_application.LoanApplicationDTO;
import net.banking.loanservice.enums.LoanStatus;

import java.time.LocalDate;

public record LoanResponse(
        Long id,
        Double principleAmount,
        Double remainingBalance,
        Double monthlyInstallment,
        LoanStatus status,
        LocalDate startedDate,
        LocalDate endDate,
        Double interest,
        String bankAccountRib,
        LoanApplicationDTO loanApplication,
        Customer customer) {}
