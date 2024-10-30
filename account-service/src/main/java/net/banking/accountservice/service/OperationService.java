package net.banking.accountservice.service;

import net.banking.accountservice.dto.bankaccount.BankAccountDetails;
import net.banking.accountservice.dto.operation.OperationRequest;
import net.banking.accountservice.dto.operation.OperationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface OperationService {
    Page<OperationResponse> getAllOperations(Double amount, Double minAmount, Double maxAmount, String transactionType, String rib, String customerIdentity, LocalDateTime startDate, LocalDateTime endDate, String createdAt, Pageable pageable);
    void transferOperation(OperationRequest request);
    BankAccountDetails bankAccountHistory(String rib);
}
