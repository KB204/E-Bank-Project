package net.banking.loanservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.banking.loanservice.dto.external_services.Customer;
import net.banking.loanservice.enums.ApplicationStatus;
import net.banking.loanservice.enums.LoanType;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
public class LoanApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String identifier;
    @Enumerated(EnumType.STRING)
    private LoanType loanType;
    private Integer loanTerm;
    private Double requestedAmount;
    private Double interest;
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;
    private LocalDate createdAt;
    private LocalDate updateAt;
    private String customerIdentity;
    private String customerEmail;
    @OneToOne(mappedBy = "loanApplication",cascade = CascadeType.ALL)
    private Loan loan;
    @Transient
    private Customer customer;
}
