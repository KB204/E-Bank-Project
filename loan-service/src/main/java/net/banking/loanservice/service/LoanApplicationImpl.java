package net.banking.loanservice.service;

import net.banking.loanservice.dao.LoanApplicationRepository;
import net.banking.loanservice.dto.loan_application.LoanApplicationRequest;
import net.banking.loanservice.dto.loan_application.LoanApplicationResponse;
import net.banking.loanservice.mapper.LoanApplicationMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoanApplicationImpl implements LoanApplicationService{
    private final LoanApplicationRepository repository;
    private final LoanApplicationMapper mapper;

    public LoanApplicationImpl(LoanApplicationRepository repository, LoanApplicationMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }
    @Override
    public List<LoanApplicationResponse> getAllLoansApplications() {
        return repository.findAll()
                .stream()
                .map(mapper::loanApplicationToDtoResponse)
                .toList();
    }
    @Override
    public void createNewLoanApplication(LoanApplicationRequest request) {

    }
}
