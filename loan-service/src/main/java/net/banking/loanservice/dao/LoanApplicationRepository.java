package net.banking.loanservice.dao;

import net.banking.loanservice.entities.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface LoanApplicationRepository extends JpaRepository<LoanApplication,Long>, JpaSpecificationExecutor<LoanApplication> {
    Optional<LoanApplication> findByIdentifierIgnoreCase(String identifier);
}
