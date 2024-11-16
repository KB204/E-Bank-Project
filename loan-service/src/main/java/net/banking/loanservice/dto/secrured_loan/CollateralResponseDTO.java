package net.banking.loanservice.dto.secrured_loan;

import net.banking.loanservice.enums.CollateralType;

public record CollateralResponseDTO(CollateralType type,boolean isVerified) {
}
