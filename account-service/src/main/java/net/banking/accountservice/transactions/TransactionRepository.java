package net.banking.accountservice.transactions;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<BankAccountTransaction,Long> {
}
