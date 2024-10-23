package net.banking.accountservice.bankaccount;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.banking.accountservice.transactions.BankAccountTransaction;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@PrimaryKeyJoinColumn(name = "id")
@Entity
public class CurrentAccount extends BankAccount{
    private Double overDraftLimit;
    private Double overDraftFees;
    @OneToMany(mappedBy = "currentAccount")
    private List<BankAccountTransaction> transactions;
}
