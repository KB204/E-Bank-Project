package net.banking.accountservice.transactions;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.banking.accountservice.bankaccount.CurrentAccount;
import net.banking.accountservice.bankaccount.Customer;

import java.time.LocalDateTime;
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class BankAccountTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String identifier;
    private LocalDateTime createdAt;
    private Double amount;
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    @ManyToOne(fetch = FetchType.LAZY)
    private CurrentAccount currentAccount;
    @Transient
    private Customer customer;
}
