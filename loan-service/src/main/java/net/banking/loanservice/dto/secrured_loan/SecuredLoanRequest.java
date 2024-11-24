package net.banking.loanservice.dto.secrured_loan;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import net.banking.loanservice.enums.CollateralType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record SecuredLoanRequest(
        @NotEmpty(message = "Le RIB est obligatoire")
        String rib,
        @NotEmpty(message = "L'identité du client est obligatoire")
        String identity,
        @NotEmpty(message = "L'identifiant de la demande est obligatoire")
        String identifier,
        @NotNull(message = "Les pièces jointes sont obligatoire")
        List<MultipartFile> files,
        @NotEmpty(message = "La description de la garantie est obligatoire")
        String description,
        @NotNull(message = "La vérification de la garantie est obligatoire")
        Boolean isVerified,
        @NotNull(message = "La valeur de la garantie est obligatoire")
        Double value,
        @NotNull(message = "Le type de la garantie est obligatoire")
        CollateralType type) {}
