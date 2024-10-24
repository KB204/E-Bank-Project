package net.banking.accountservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@PrimaryKeyJoinColumn(name = "id")
@Entity
public class SavingAccount extends BankAccount{
    private Double interest;
    private Double withDrawLimit;
}
