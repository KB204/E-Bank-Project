package net.banking.loanservice.dao;

import net.banking.loanservice.entities.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan,Long>, JpaSpecificationExecutor<Loan> {
    Optional<Loan> findByLoanApplication_Identifier(String identifier);
}
