package net.banking.loanservice.service;

import net.banking.loanservice.client.BankAccountRestClient;
import net.banking.loanservice.client.CustomerRestClient;
import net.banking.loanservice.dao.LoanApplicationRepository;
import net.banking.loanservice.dao.LoanRepository;
import net.banking.loanservice.dto.external_services.BankAccount;
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
import net.banking.loanservice.service.specification.LoanSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LoanServiceImpl implements LoanService{
    private final LoanRepository loanRepository;
    private final LoanApplicationRepository loanApplicationRepository;
    private final CustomerRestClient restClient;
    private final BankAccountRestClient bankAccountRestClient;
    private final LoanMapper mapper;

    public LoanServiceImpl(LoanRepository loanRepository, LoanApplicationRepository loanApplicationRepository, CustomerRestClient restClient, BankAccountRestClient bankAccountRestClient, LoanMapper mapper) {
        this.loanRepository = loanRepository;
        this.loanApplicationRepository = loanApplicationRepository;
        this.restClient = restClient;
        this.bankAccountRestClient = bankAccountRestClient;
        this.mapper = mapper;
    }

    @Override
    public Page<LoanResponse> findAllLoans(String identifier, Double amount, String status, LocalDate started,LocalDate ended,
                                           LocalDate start, LocalDate end, Pageable pageable) {

        Specification<Loan> specification = LoanSpecification.filterWithoutAnyConditions()
                .and(LoanSpecification.identifierEqual(identifier))
                .and(LoanSpecification.amountEqual(amount))
                .and(LoanSpecification.statusLike(status))
                .and(LoanSpecification.startedDateLike(started))
                .and(LoanSpecification.endDateLike(ended))
                .and(LoanSpecification.loanBetween(start, end));
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("startedDate").descending());

        return loanRepository.findAll(specification,pageable)
                .map(loan -> {
                    loan.setCustomer(restClient.fetchCustomerByIdentity(loan.getLoanApplication().getCustomerIdentity()));
                    return mapper.loanObjectToDtoResponse(loan);
                });
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
    public Page<UnsecuredLoanResponse> findAllUnsecuredLoans(String identifier, Double amount, String status, LocalDate started,LocalDate ended,
                                                             LocalDate start, LocalDate end, Pageable pageable) {

        Specification<Loan> specification = LoanSpecification.unsecuredLoansOnly()
                .and(LoanSpecification.identifierEqual(identifier))
                .and(LoanSpecification.amountEqual(amount))
                .and(LoanSpecification.statusLike(status))
                .and(LoanSpecification.startedDateLike(started))
                .and(LoanSpecification.endDateLike(ended))
                .and(LoanSpecification.loanBetween(start, end));
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("startedDate").descending());

        return loanRepository.findAll(specification,pageable)
                .map(loan -> {
                    UnsecuredLoan unsecuredLoan = (UnsecuredLoan) loan;
                    unsecuredLoan.setCustomer(restClient.fetchCustomerByIdentity(unsecuredLoan.getLoanApplication().getCustomerIdentity()));
                    return mapper.unsecuredLoanToDtoResponse(unsecuredLoan);
                });
    }

    @Override
    public void createSecuredLoan(SecuredLoanRequest request) {

    }

    @Override
    public void createUnsecuredLoan(UnsecuredLoanRequest request) {
        LoanApplication loanApplication = loanApplicationRepository.findByIdentifierIgnoreCase(request.identifier())
                .orElseThrow(() -> new ResourceNotFoundException(String.format("La demande identifiée par %s n'existe pas",request.identifier())));
        BankAccount bankAccount = bankAccountRestClient.findBankAccount(request.rib(), request.identity());

        checkBusinessRules(loanApplication);

        UnsecuredLoan loan = UnsecuredLoan.builder()
                .status(LoanStatus.ACTIVE)
                .principleAmount(loanApplication.getRequestedAmount())
                .remainingBalance(loanApplication.getRequestedAmount())
                .interest(loanApplication.getInterest())
                .startedDate(LocalDate.now())
                .bankAccountRib(bankAccount.rib())
                .loanApplication(loanApplication)
                .build();

        Double treat = calculateMonthlyInstallment(loan);
        LocalDate endDate = calculateLoanEndingDate(loan);

        loan.setMonthlyInstallment(treat);
        loan.setEndDate(endDate);
        checkBusinessRules(loan);

        loanRepository.save(loan);
    }
    private LocalDate calculateLoanEndingDate(Loan loan){
        return loan.getStartedDate().plusMonths(loan.getLoanApplication().getLoanTerm());
    }
    private Double calculateMonthlyInstallment(Loan loan){
        double monthlyInterest = (loan.getInterest() / 100) / 12;
        int loanTerm = loan.getLoanApplication().getLoanTerm();
        double treat = (loan.getPrincipleAmount() * monthlyInterest * Math.pow(1 + monthlyInterest, loanTerm)) /
                (Math.pow(1 + monthlyInterest, loanTerm) - 1);

        return BigDecimal.valueOf(treat)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
    private void checkBusinessRules(LoanApplication loanApplication){
        if (loanApplication.getStatus().equals(ApplicationStatus.REJECTED))
            throw new BankAccountException(String.format("La demande identifiée par %s a été rejetée",loanApplication.getIdentifier()));
        if (loanApplication.getStatus().equals(ApplicationStatus.PENDING))
            throw new BankAccountException(String.format("La demande identifiée par %s est en cours de traitement",loanApplication.getIdentifier()));
    }
    private void checkBusinessRules(Loan loan){
        Optional.ofNullable(loan.getRemainingBalance())
                .filter(remainingBalance -> Double.compare(remainingBalance,0.0) == 0)
                .ifPresent(remainingBalance -> loan.setStatus(LoanStatus.CLOSED));
    }
}
