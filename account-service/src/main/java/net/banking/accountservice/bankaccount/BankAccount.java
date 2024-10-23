package net.banking.accountservice.bankaccount;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@NoArgsConstructor
@AllArgsConstructor
@Data
@Inheritance(strategy = InheritanceType.JOINED)
@Entity
abstract class BankAccount {
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
}
