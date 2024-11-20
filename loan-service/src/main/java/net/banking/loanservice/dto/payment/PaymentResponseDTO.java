package net.banking.loanservice.dto.payment;


import net.banking.loanservice.enums.PaymentStatus;

import java.time.LocalDateTime;


public record PaymentResponseDTO(Double amountPaid, LocalDateTime paymentDate, PaymentStatus status) {}
