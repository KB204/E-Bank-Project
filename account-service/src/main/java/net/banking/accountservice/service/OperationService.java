package net.banking.accountservice.service;

import net.banking.accountservice.dto.bankaccount.BankAccountDetails;
import net.banking.accountservice.dto.operation.CompleteOperationDTO;
import net.banking.accountservice.dto.operation.OperationRequest;
import net.banking.accountservice.dto.operation.OperationResponse;
import net.banking.accountservice.dto.operation.WithdrawRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface OperationService {
    Page<OperationResponse> getAllOperations(Double amount, Double minAmount, Double maxAmount, String transactionType, String rib, String customerIdentity, LocalDateTime startDate, LocalDateTime endDate, String createdAt, Pageable pageable);
    void transferOperation(String rib, OperationRequest request);
    void completeTransferOperation(String rib, CompleteOperationDTO request);
    void withdrawalOperation(WithdrawRequest request);
    BankAccountDetails bankAccountHistory(String rib,Double amount,String transactionType,LocalDateTime startDate,LocalDateTime endDate,Pageable pageable);
}
