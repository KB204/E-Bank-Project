package net.banking.accountservice.bankaccount;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@PrimaryKeyJoinColumn(name = "id")
@Entity
class SavingAccount extends BankAccount{
    private Double interest;
    private Double withDrawLimit;
}
