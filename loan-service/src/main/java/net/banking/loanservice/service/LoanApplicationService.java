package net.banking.loanservice.service;

import net.banking.loanservice.dto.loan_application.LoanApplicationRequest;
import net.banking.loanservice.dto.loan_application.LoanApplicationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LoanApplicationService {
    void createNewLoanApplication(LoanApplicationRequest request);
    void approveLoanApplication(String identifier);
    void declineLoanApplication(String identifier);
    LoanApplicationResponse findLoanApplication(String identifier);
    void removeLoanApplication(Long id);
    Page<LoanApplicationResponse> getAllLoansApplications(String identifier, String loanType, Integer loanTerm, Double amount,
                                                          Double minAmount, Double maxAmount, String status, String customerIdentity, Pageable pageable);
}
