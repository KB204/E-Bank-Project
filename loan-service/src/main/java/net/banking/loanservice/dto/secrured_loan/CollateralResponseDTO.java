package net.banking.loanservice.dto.secrured_loan;

import net.banking.loanservice.enums.CollateralType;

public record CollateralResponseDTO(String url,String description,Boolean isVerified,Double value,CollateralType type) {}
