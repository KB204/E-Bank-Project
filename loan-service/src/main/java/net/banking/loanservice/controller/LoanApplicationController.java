package net.banking.loanservice.controller;

import net.banking.loanservice.dto.loan_application.LoanApplicationResponse;
import net.banking.loanservice.service.LoanApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/loanApplications")
public class LoanApplicationController {
    private final LoanApplicationService service;

    public LoanApplicationController(LoanApplicationService service) {
        this.service = service;
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<LoanApplicationResponse> findAllLoansApplications() {
        return service.getAllLoansApplications();
    }
}
