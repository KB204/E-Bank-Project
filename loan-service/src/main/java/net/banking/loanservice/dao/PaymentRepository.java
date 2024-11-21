package net.banking.loanservice.dao;

import net.banking.loanservice.entities.Loan;
import net.banking.loanservice.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Loan>, JpaSpecificationExecutor<Payment> {
    Optional<Payment> findById(Long id);
}
