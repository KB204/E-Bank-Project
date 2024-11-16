package net.banking.loanservice.service;

import net.banking.loanservice.dto.loan.LoanResponse;
import net.banking.loanservice.dto.secrured_loan.SecuredLoanRequest;
import net.banking.loanservice.dto.secrured_loan.SecuredLoanResponse;
import net.banking.loanservice.dto.unsecured_loan.UnsecuredLoanRequest;
import net.banking.loanservice.dto.unsecured_loan.UnsecuredLoanResponse;

import java.util.List;

public interface LoanService {
    List<LoanResponse> findAllLoans();
    List<SecuredLoanResponse> findAllSecuredLoans();
    List<UnsecuredLoanResponse> findAllUnsecuredLoans();
    void createSecuredLoan(SecuredLoanRequest request);
    void createUnsecuredLoan(UnsecuredLoanRequest request);
}
