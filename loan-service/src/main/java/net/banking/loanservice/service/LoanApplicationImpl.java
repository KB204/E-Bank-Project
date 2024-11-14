package net.banking.loanservice.service;

import net.banking.loanservice.client.CustomerRest;
import net.banking.loanservice.dao.LoanApplicationRepository;
import net.banking.loanservice.dto.Customer;
import net.banking.loanservice.dto.loan_application.LoanApplicationRequest;
import net.banking.loanservice.dto.loan_application.LoanApplicationResponse;
import net.banking.loanservice.entities.LoanApplication;
import net.banking.loanservice.enums.ApplicationStatus;
import net.banking.loanservice.exceptions.ResourceAlreadyExists;
import net.banking.loanservice.mapper.LoanApplicationMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class LoanApplicationImpl implements LoanApplicationService{
    private final LoanApplicationRepository repository;
    private final LoanApplicationMapper mapper;
    private final CustomerRest rest;

    public LoanApplicationImpl(LoanApplicationRepository repository, LoanApplicationMapper mapper, CustomerRest rest) {
        this.repository = repository;
        this.mapper = mapper;
        this.rest = rest;
    }
    @Override
    public List<LoanApplicationResponse> getAllLoansApplications() {
        return repository.findAll()
                .stream()
                .map(loanApplication -> {
                    loanApplication.setCustomer(rest.getCustomerByIdentity(loanApplication.getCustomerIdentity()));
                    return mapper.loanApplicationToDtoResponse(loanApplication);
                })
                .toList();
    }
    @Override
    public void createNewLoanApplication(LoanApplicationRequest request) {
        Customer customer = rest.findCustomer(request.customerIdentity());
        LoanApplication loanApplication = LoanApplication.builder()
                .identifier(UUID.randomUUID().toString().substring(0,10))
                .loanType(request.type())
                .loanTerm(request.term())
                .requestedAmount(request.amount())
                .interest(5.0)
                .createdAt(LocalDate.now())
                .status(ApplicationStatus.PENDING)
                .customerIdentity(customer.identity())
                .customerEmail(customer.email())
                .build();
        repository.findByIdentifierIgnoreCase(loanApplication.getIdentifier())
                .ifPresent(loan -> {
                    throw new ResourceAlreadyExists("Identifiant de la demande existant déjà, veuillez soumettre une autre demande "
                            +loanApplication.getIdentifier());
                });
        repository.save(loanApplication);
    }
}
