package net.banking.loanservice.service;

import net.banking.loanservice.client.CustomerRestClient;
import net.banking.loanservice.dao.LoanApplicationRepository;
import net.banking.loanservice.dto.Customer;
import net.banking.loanservice.dto.loan_application.LoanApplicationRequest;
import net.banking.loanservice.dto.loan_application.LoanApplicationResponse;
import net.banking.loanservice.entities.LoanApplication;
import net.banking.loanservice.enums.ApplicationStatus;
import net.banking.loanservice.exceptions.ResourceAlreadyExists;
import net.banking.loanservice.exceptions.ResourceNotFoundException;
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
    private final SendNotificationService notificationService;
    private final LoanApplicationMapper mapper;
    private final CustomerRestClient rest;

    public LoanApplicationImpl(LoanApplicationRepository repository, SendNotificationService notificationService, LoanApplicationMapper mapper, CustomerRestClient rest) {
        this.repository = repository;
        this.notificationService = notificationService;
        this.mapper = mapper;
        this.rest = rest;
    }
    @Override
    public List<LoanApplicationResponse> getAllLoansApplications() {
        return repository.findAll()
                .stream()
                .map(loanApplication -> {
                    loanApplication.setCustomer(rest.fetchCustomerByIdentity(loanApplication.getCustomerIdentity()));
                    return mapper.loanApplicationToDtoResponse(loanApplication);
                })
                .toList();
    }
    @Override
    public void createNewLoanApplication(LoanApplicationRequest request) {
        Customer customer = rest.findCustomerByIdentity(request.customerIdentity());
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
        notificationService.loanPendingNotification(loanApplication.getIdentifier());
    }

    @Override
    public void approveLoanApplication(String identifier) {
        LoanApplication loanApplication = repository.findByIdentifierIgnoreCase(identifier)
                .orElseThrow(() -> new ResourceNotFoundException("Demande n'existe pas"));

        loanApplication.setStatus(ApplicationStatus.APPROVED);
        loanApplication.setUpdateAt(LocalDate.now());
        repository.save(loanApplication);
        notificationService.loanApprovedNotification(identifier);
    }

    @Override
    public void declineLoanApplication(String identifier) {
        LoanApplication loanApplication = repository.findByIdentifierIgnoreCase(identifier)
                .orElseThrow(() -> new ResourceNotFoundException("Demande n'existe pas"));

        loanApplication.setStatus(ApplicationStatus.REJECTED);
        loanApplication.setUpdateAt(LocalDate.now());
        repository.save(loanApplication);
        notificationService.loanDeclinedNotification(identifier);
    }

    @Override
    public LoanApplicationResponse findLoanApplication(String identifier) {
        LoanApplication loanApplication = repository.findByIdentifierIgnoreCase(identifier)
                .orElseThrow(() -> new ResourceNotFoundException("Demande n'existe pas"));
        Customer customer = rest.fetchCustomerByIdentity(loanApplication.getCustomerIdentity());
        loanApplication.setCustomer(customer);
        return mapper.loanApplicationToDtoResponse(loanApplication);
    }

    @Override
    public void removeLoanApplication(Long id) {
        LoanApplication loanApplication = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Demande n'existe pas"));
        repository.delete(loanApplication);
    }
}
