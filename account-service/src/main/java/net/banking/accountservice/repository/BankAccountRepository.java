package net.banking.accountservice.repository;

import net.banking.accountservice.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount,Long> , JpaSpecificationExecutor<BankAccount> {
    Optional<BankAccount> findByRibIgnoreCase(String rib);
}
