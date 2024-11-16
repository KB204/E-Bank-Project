package net.banking.loanservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@PrimaryKeyJoinColumn(name = "id")
@Entity
public class SecuredLoan extends Loan{
    @ElementCollection
    @CollectionTable(name = "collaterals_loan",joinColumns = @JoinColumn(name = "secured_loan_id"))
    private List<Collateral> collaterals = new ArrayList<>();
}
