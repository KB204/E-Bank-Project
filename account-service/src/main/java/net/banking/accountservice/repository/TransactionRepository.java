package net.banking.accountservice.repository;

import net.banking.accountservice.model.BankAccountTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TransactionRepository extends JpaRepository<BankAccountTransaction,Long> , JpaSpecificationExecutor<BankAccountTransaction> {
    List<BankAccountTransaction> findByBankAccount_Rib(String rib);
}
