package net.banking.accountservice.dto.operation;

import jakarta.validation.constraints.NotNull;

public record WithdrawRequest(@NotNull(message = "Le montant de l'opération est obligatoire") Double amount) {}
