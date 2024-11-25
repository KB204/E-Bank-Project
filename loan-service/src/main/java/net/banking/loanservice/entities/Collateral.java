package net.banking.loanservice.entities;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.banking.loanservice.enums.CollateralType;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Collateral{
        private String url;
        private String description;
        private Boolean isVerified;
        private Double value;
        @Enumerated(EnumType.STRING)
        private CollateralType type;
}
