package net.banking.loanservice.dto.payment;


import net.banking.loanservice.enums.PaymentStatus;


public record PaymentResponseDTO(Double amountPaid, PaymentStatus status) {}
