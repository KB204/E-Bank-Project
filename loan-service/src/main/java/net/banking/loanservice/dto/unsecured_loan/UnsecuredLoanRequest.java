package net.banking.loanservice.dto.unsecured_loan;

import jakarta.validation.constraints.NotEmpty;

public record UnsecuredLoanRequest(
        @NotEmpty(message = "Le RIB est obligatoire")
        String rib,
        @NotEmpty(message = "L'identité du client est obligatoire")
        String identity,
        @NotEmpty(message = "L'identifiant de la demande est obligatoire")
        String identifier) {}
