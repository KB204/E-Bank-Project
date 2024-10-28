package net.banking.accountservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import net.banking.accountservice.dto.Customer;
import net.banking.accountservice.enums.AccountStatus;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED)
@Entity
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String rib;
    private Double balance;
    private String currency;
    private String branch;
    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;
    @Transient
    private Customer customer;
    private String customerIdentity;
    @OneToMany(mappedBy = "bankAccount")
    private List<BankAccountTransaction> transactions;
}
