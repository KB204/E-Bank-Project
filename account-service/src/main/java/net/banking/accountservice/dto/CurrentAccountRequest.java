package net.banking.accountservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CurrentAccountRequest(
        @NotNull(message = "Le solde est obligatoire")
        @Min(value = 100,message = "Le solde ne peut pas être inférieur à 100")
        Double balance,
        @NotEmpty(message = "Le devise est obligatoire")
        String currency,
        @NotEmpty(message = "La domiciliation du compte est obligatoire")
        String branch,
        @NotEmpty(message = "Le client est obligatoire")
        String identity) {}
