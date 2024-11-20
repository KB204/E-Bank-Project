package net.banking.loanservice.dto.loan;

import lombok.Builder;
import net.banking.loanservice.dto.payment.PaymentResponseDTO;
import net.banking.loanservice.enums.LoanStatus;

import java.util.List;
@Builder
public record LoanDetailsDTO(
        String identifier,
        Double principleAmount,
        Double monthlyInstallment,
        LoanStatus status,
        String bankAccountRib,
        List<PaymentResponseDTO> payments) {}
