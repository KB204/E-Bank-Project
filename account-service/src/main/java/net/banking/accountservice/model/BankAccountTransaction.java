package net.banking.accountservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.banking.accountservice.enums.TransactionType;
import net.banking.accountservice.dto.Customer;

import java.time.LocalDateTime;
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
public class BankAccountTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String motif;
    private String description;
    private LocalDateTime createdAt;
    private Double amount;
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    @ManyToOne(fetch = FetchType.LAZY)
    private BankAccount bankAccount;
    @Transient
    private Customer customer;
}
