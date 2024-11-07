package net.banking.accountservice.dto.operation;

import jakarta.validation.constraints.NotNull;

public record CompleteWithdrawDTO(
        @NotNull(message = "Le montant de l'opération est obligatoire") Double amount,
        @NotNull(message = "Le code de vérification est obligatoire")
        Integer otp) {}
