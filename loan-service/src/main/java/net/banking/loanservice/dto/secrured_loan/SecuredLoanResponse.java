package net.banking.loanservice.dto.secrured_loan;

import net.banking.loanservice.dto.Customer;
import net.banking.loanservice.dto.loan_application.LoanApplicationDTO;
import net.banking.loanservice.dto.payment.PaymentResponseDTO;
import net.banking.loanservice.enums.LoanStatus;

import java.time.LocalDate;
import java.util.List;

public record SecuredLoanResponse(
        Long id,
        Double principleAmount,
        Double remainingBalance,
        Double monthlyInstallment,
        LoanStatus status,
        LocalDate startedDate,
        LocalDate endDate,
        Double interest,
        LoanApplicationDTO loanApplication,
        List<PaymentResponseDTO> payments,
        List<CollateralResponseDTO> collaterals,
        Customer customer) {}