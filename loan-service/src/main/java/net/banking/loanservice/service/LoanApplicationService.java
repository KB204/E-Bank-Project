package net.banking.loanservice.service;

import net.banking.loanservice.dto.loan_application.LoanApplicationRequest;
import net.banking.loanservice.dto.loan_application.LoanApplicationResponse;

import java.util.List;

public interface LoanApplicationService {
    void createNewLoanApplication(LoanApplicationRequest request);
    void approveLoanApplication(String identifier);
    void declineLoanApplication(String identifier);
    LoanApplicationResponse findLoanApplication(String identifier);
    void removeLoanApplication(Long id);
    List<LoanApplicationResponse> getAllLoansApplications();
}
