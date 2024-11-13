package net.banking.loanservice.service;

import net.banking.loanservice.dto.loan_application.LoanApplicationRequest;
import net.banking.loanservice.dto.loan_application.LoanApplicationResponse;

import java.util.List;

public interface LoanApplicationService {
    void createNewLoanApplication(LoanApplicationRequest request);
    List<LoanApplicationResponse> getAllLoansApplications();
}
