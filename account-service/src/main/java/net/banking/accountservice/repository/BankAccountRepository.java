package net.banking.accountservice.repository;

import net.banking.accountservice.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount,Long> {
    Optional<BankAccount> findByRibIgnoreCase(String rib);
}
