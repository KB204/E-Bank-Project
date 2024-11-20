package net.banking.loanservice.service;

import net.banking.loanservice.dto.loan.LoanResponse;
import net.banking.loanservice.dto.secrured_loan.SecuredLoanRequest;
import net.banking.loanservice.dto.secrured_loan.SecuredLoanResponse;
import net.banking.loanservice.dto.unsecured_loan.UnsecuredLoanRequest;
import net.banking.loanservice.dto.unsecured_loan.UnsecuredLoanResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface LoanService {
    Page<LoanResponse> findAllLoans(String identifier, Double amount, String status, LocalDate started, LocalDate ended,
                                    LocalDate start, LocalDate end, Pageable pageable);
    List<SecuredLoanResponse> findAllSecuredLoans();
    Page<UnsecuredLoanResponse> findAllUnsecuredLoans(String identifier, Double amount, String status, LocalDate started,LocalDate ended,
                                                      LocalDate start, LocalDate end, Pageable pageable);
    void createSecuredLoan(SecuredLoanRequest request);
    void createUnsecuredLoan(UnsecuredLoanRequest request);
}
