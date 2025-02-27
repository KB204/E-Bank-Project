package net.banking.accountservice.dto.operation;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CompleteOperationDTO(
        @NotEmpty(message = "Le rib du bénéficiaire est obligatoire")
        String ribTo,
        @NotNull(message = "Le montant de l'opération est obligatoire")
        Double amount,
        @NotEmpty(message = "Le motif est obligatoire")
        String motif,
        @NotEmpty(message = "L'identité de l'expéditeur est obligatoire")
        String senderIdentity,
        @NotEmpty(message = "L'identité du bénéficiaire est obligatoire")
        String receiverIdentity,
        @NotNull(message = "Le code de vérification est obligatoire")
        Integer otp) {}
