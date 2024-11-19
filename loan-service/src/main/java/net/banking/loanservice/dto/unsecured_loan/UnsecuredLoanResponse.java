package net.banking.loanservice.dto.unsecured_loan;

import net.banking.loanservice.dto.external_services.Customer;
import net.banking.loanservice.dto.loan_application.LoanApplicationDTO;
import net.banking.loanservice.dto.payment.PaymentResponseDTO;
import net.banking.loanservice.enums.LoanStatus;

import java.time.LocalDate;
import java.util.List;

public record UnsecuredLoanResponse(
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
