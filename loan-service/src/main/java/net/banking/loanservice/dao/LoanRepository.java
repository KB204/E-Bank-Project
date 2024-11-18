package net.banking.loanservice.dao;

import net.banking.loanservice.entities.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan,Long> {
    Optional<Loan> findByLoanApplication_Identifier(String identifier);
}
