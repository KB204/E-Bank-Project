package net.banking.accountservice.dto.operation;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record WithdrawRequest(
        @NotEmpty(message = "Le rib est obligatoire")
        String ribFrom,
        @NotNull(message = "Le montant de l'opération est obligatoire")
        Double amount) {}
