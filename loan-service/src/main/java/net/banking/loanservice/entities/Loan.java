package net.banking.loanservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import net.banking.loanservice.dto.Customer;
import net.banking.loanservice.enums.LoanStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
@Inheritance(strategy = InheritanceType.JOINED)
@Entity
public abstract class Loan{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double principleAmount;
    private Double remainingBalance;
    private Double monthlyInstallment;
    @Enumerated(EnumType.STRING)
    private LoanStatus status;
    private LocalDate startedDate;
    private LocalDate endDate;
    private Double interest;
    @OneToOne
    @JoinColumn(name = "loan_application_id", referencedColumnName = "id")
    private LoanApplication loanApplication;
    @OneToMany(mappedBy = "loan")
    private List<Payment> payments = new ArrayList<>();
    @Transient
    private Customer customer;
}
