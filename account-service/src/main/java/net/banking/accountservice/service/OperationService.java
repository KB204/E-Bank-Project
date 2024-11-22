package net.banking.accountservice.service;

import net.banking.accountservice.dto.DebitAccountRequest;
import net.banking.accountservice.dto.bankaccount.BankAccountDetails;
import net.banking.accountservice.dto.operation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface OperationService {
    Page<OperationResponse> getAllOperations(Double amount, Double minAmount, Double maxAmount, String transactionType, String rib, String customerIdentity, LocalDateTime startDate, LocalDateTime endDate, String createdAt, Pageable pageable);
    void transferOperation(String rib, OperationRequest request);
    void completeTransferOperation(String rib, CompleteOperationDTO request);
    void withdrawalOperation(String rib , WithdrawRequest request);
    void completeWithdrawalOperation(String rib , CompleteWithdrawDTO request);
    void debitAccount(DebitAccountRequest request);
    BankAccountDetails bankAccountHistory(String rib,Double amount,String transactionType,LocalDateTime startDate,LocalDateTime endDate,Pageable pageable);
}
