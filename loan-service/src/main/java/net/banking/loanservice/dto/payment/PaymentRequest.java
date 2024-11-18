package net.banking.loanservice.dto.payment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record PaymentRequest(
        @NotEmpty(message = "L'identifiant du crédit est obligatoire")
        String identifier,
        @NotNull(message = "Le montant a payer est obligatoire")
        @Min(value = 100,message = "Le montant ne peut pas être inférieur à 100 MAD")
        Double amount) {}
