package net.banking.accountservice.repository;

import net.banking.accountservice.model.CodeVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CodeVerificationRepository extends JpaRepository<CodeVerification,Long> {
    Optional<CodeVerification> findByCodeAndBankAccount_Rib(Integer code,String rib);
}
