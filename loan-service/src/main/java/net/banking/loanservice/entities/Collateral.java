package net.banking.loanservice.entities;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import net.banking.loanservice.enums.CollateralType;

@Builder
@Embeddable
public record Collateral(
        String url,
        String description,
        Boolean isVerified,
        Double value,
        @Enumerated(EnumType.STRING)
        CollateralType type) {}
