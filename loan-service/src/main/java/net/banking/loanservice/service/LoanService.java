package net.banking.loanservice.service;

import net.banking.loanservice.dto.loan.LoanResponse;
import net.banking.loanservice.dto.secrured_loan.SecuredLoanRequest;
import net.banking.loanservice.dto.secrured_loan.SecuredLoanResponse;
import net.banking.loanservice.dto.unsecured_loan.UnsecuredLoanRequest;
import net.banking.loanservice.dto.unsecured_loan.UnsecuredLoanResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface LoanService {
    Page<LoanResponse> findAllLoans(String identifier, Double amount, String status, String started, String ended,
                                    LocalDate start, LocalDate end, Pageable pageable);
    List<SecuredLoanResponse> findAllSecuredLoans();
    Page<UnsecuredLoanResponse> findAllUnsecuredLoans(String identifier, Double amount, String status, String started,String ended,
                                                      LocalDate start, LocalDate end, Pageable pageable);
    void createSecuredLoan(SecuredLoanRequest request,List<MultipartFile> files);
    void createUnsecuredLoan(UnsecuredLoanRequest request);
}
