package net.banking.loanservice.dao;

import net.banking.loanservice.entities.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoanApplicationRepository extends JpaRepository<LoanApplication,Long> {
    Optional<LoanApplication> findByIdentifierIgnoreCase(String identifier);
}
