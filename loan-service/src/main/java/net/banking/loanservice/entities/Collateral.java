package net.banking.loanservice.entities;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import net.banking.loanservice.enums.CollateralType;

@Embeddable
public record Collateral(
        String url,
        String description,
        boolean isVerified,
        Double value,
        @Enumerated(EnumType.STRING)
        CollateralType type) {}
