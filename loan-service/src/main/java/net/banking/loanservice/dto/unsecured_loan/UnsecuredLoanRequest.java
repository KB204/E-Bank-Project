package net.banking.loanservice.dto.unsecured_loan;

import jakarta.validation.constraints.NotEmpty;

public record UnsecuredLoanRequest(
        @NotEmpty(message = "L'identifiant de la demande est obligatoire")
        String identifier) {}
