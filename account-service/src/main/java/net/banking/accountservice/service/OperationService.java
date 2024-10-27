package net.banking.accountservice.service;

import net.banking.accountservice.dto.operation.OperationRequest;
import net.banking.accountservice.dto.operation.OperationResponse;

import java.util.List;

public interface OperationService {
    List<OperationResponse> getAllOperations();
    void transferOperation(OperationRequest request);
}
