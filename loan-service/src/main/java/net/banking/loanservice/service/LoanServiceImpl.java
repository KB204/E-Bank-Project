package net.banking.loanservice.service;

import net.banking.loanservice.client.CustomerRestClient;
import net.banking.loanservice.dao.LoanApplicationRepository;
import net.banking.loanservice.dao.LoanRepository;
import net.banking.loanservice.dto.loan.LoanResponse;
import net.banking.loanservice.dto.secrured_loan.SecuredLoanRequest;
import net.banking.loanservice.dto.secrured_loan.SecuredLoanResponse;
import net.banking.loanservice.dto.unsecured_loan.UnsecuredLoanRequest;
import net.banking.loanservice.dto.unsecured_loan.UnsecuredLoanResponse;
import net.banking.loanservice.entities.*;
import net.banking.loanservice.enums.ApplicationStatus;
import net.banking.loanservice.enums.LoanStatus;
import net.banking.loanservice.exceptions.BankAccountException;
import net.banking.loanservice.exceptions.ResourceNotFoundException;
import net.banking.loanservice.mapper.LoanMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class LoanServiceImpl implements LoanService{
    private final LoanRepository loanRepository;
    private final LoanApplicationRepository loanApplicationRepository;
    private final CustomerRestClient restClient;
    private final LoanMapper mapper;

    public LoanServiceImpl(LoanRepository loanRepository, LoanApplicationRepository loanApplicationRepository, CustomerRestClient restClient, LoanMapper mapper) {
        this.loanRepository = loanRepository;
        this.loanApplicationRepository = loanApplicationRepository;
        this.restClient = restClient;
        this.mapper = mapper;
    }

    @Override
    public List<LoanResponse> findAllLoans() {
        return loanRepository.findAll()
                .stream()
                .map(loan -> {
                    loan.setCustomer(restClient.fetchCustomerByIdentity(loan.getLoanApplication().getCustomerIdentity()));
                    return mapper.loanObjectToDtoResponse(loan);
                })
                .toList();
    }

    @Override
    public List<SecuredLoanResponse> findAllSecuredLoans() {
        return loanRepository.findAll()
                .stream()
                .filter(loan -> loan instanceof SecuredLoan)
                .map(loan -> {
                    SecuredLoan securedLoan = (SecuredLoan) loan;
                    securedLoan.setCustomer(restClient.fetchCustomerByIdentity(securedLoan.getLoanApplication().getCustomerIdentity()));
                    return mapper.securedLoanToDtoResponse(securedLoan);
                })
                .toList();
    }

    @Override
    public List<UnsecuredLoanResponse> findAllUnsecuredLoans() {
        return loanRepository.findAll()
                .stream()
                .filter(loan -> loan instanceof UnsecuredLoan)
                .map(loan -> {
                    UnsecuredLoan unsecuredLoan = (UnsecuredLoan) loan;
                    unsecuredLoan.setCustomer(restClient.fetchCustomerByIdentity(unsecuredLoan.getLoanApplication().getCustomerIdentity()));
                    return mapper.unsecuredLoanToDtoResponse(unsecuredLoan);
                })
                .toList();
    }

    @Override
    public void createSecuredLoan(SecuredLoanRequest request) {

    }

    @Override
    public void createUnsecuredLoan(UnsecuredLoanRequest request) {
        LoanApplication loanApplication = loanApplicationRepository.findByIdentifierIgnoreCase(request.identifier())
                .orElseThrow(() -> new ResourceNotFoundException(String.format("La demande identifiée par %s n'existe pas",request.identifier())));
        checkBusinessRules(loanApplication);

        UnsecuredLoan loan = UnsecuredLoan.builder()
                .status(LoanStatus.ACTIVE)
                .principleAmount(loanApplication.getRequestedAmount())
                .interest(loanApplication.getInterest())
                .startedDate(LocalDate.now())
                .build();
        Double amount = calculateRemainingAmount(loan);
        loan.setRemainingBalance(amount);

        loanRepository.save(loan);
    }

    private Double calculateRemainingAmount(Loan loan){

        double amount = loan.getPayments()
                .stream()
                .mapToDouble(Payment::getAmountPaid)
                .sum();

        return loan.getPrincipleAmount() - amount;
    }

    private void checkBusinessRules(LoanApplication loanApplication){
        if (loanApplication.getStatus().equals(ApplicationStatus.REJECTED))
            throw new BankAccountException(String.format("La demande identifiée par %s a été rejetée",loanApplication.getIdentifier()));
        if (loanApplication.getStatus().equals(ApplicationStatus.PENDING))
            throw new BankAccountException(String.format("La demande identifiée par %s est en cours de traitement",loanApplication.getIdentifier()));
    }
}
