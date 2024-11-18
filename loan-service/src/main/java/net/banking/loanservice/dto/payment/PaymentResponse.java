package net.banking.loanservice.dto.payment;

import net.banking.loanservice.dto.loan.LoanResponseDTO;
import net.banking.loanservice.enums.PaymentStatus;

import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        Double amountPaid,
        PaymentStatus status,
        LocalDateTime paymentDate,
        LoanResponseDTO loan) {}
