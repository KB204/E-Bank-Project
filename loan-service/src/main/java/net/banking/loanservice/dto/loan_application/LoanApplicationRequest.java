package net.banking.loanservice.dto.loan_application;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import net.banking.loanservice.enums.LoanType;

public record LoanApplicationRequest(
        @NotNull(message = "Le type de crédit est obligatoire")
        LoanType type,
        @NotNull(message = "Le durée du crédit est obligatoire")
        @Min(value = 12,message = "La durée ne peut pas être inférieure à 12 Mois")
        @Max(value = 120,message = "La durée ne peut pas être supérieure a 120 Mois")
        Integer term,
        @NotNull(message = "Le montant est obligatoire")
        @Min(value = 5000,message = "Le montant ne peut pas être inférieur à 5000")
        @Max(value = 500000,message = "Le montant ne peut pas être supérieur a 500000")
        Double amount,
        @NotEmpty(message = "L'identité du client est obligatoire")
        String customerIdentity) {}
