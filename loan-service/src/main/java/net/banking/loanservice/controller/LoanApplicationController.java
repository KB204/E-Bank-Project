package net.banking.loanservice.controller;

import jakarta.validation.Valid;
import net.banking.loanservice.dto.loan_application.LoanApplicationRequest;
import net.banking.loanservice.dto.loan_application.LoanApplicationResponse;
import net.banking.loanservice.service.LoanApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<String> saveNewLoanApplication(@RequestBody @Valid LoanApplicationRequest request) {
        service.createNewLoanApplication(request);
        return new ResponseEntity<>("Votre demande de crédit a été transmise avec succès. Vous serez informé par email pour obtenir les détails de votre demande",HttpStatus.CREATED);
    }
}
