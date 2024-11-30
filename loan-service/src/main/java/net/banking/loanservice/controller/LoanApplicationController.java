package net.banking.loanservice.controller;

import jakarta.validation.Valid;
import net.banking.loanservice.dto.loan_application.LoanApplicationRequest;
import net.banking.loanservice.dto.loan_application.LoanApplicationResponse;
import net.banking.loanservice.service.LoanApplicationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/loanApplications")
public class LoanApplicationController {
    private final LoanApplicationService service;

    public LoanApplicationController(LoanApplicationService service) {
        this.service = service;
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('AGENT')")
    public Page<LoanApplicationResponse> findAllLoansApplications(
            @RequestParam(required = false) String identifier,
            @RequestParam(required = false) String loanType,
            @RequestParam(required = false) Integer loanTerm,
            @RequestParam(required = false) Double amount,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String customerIdentity,
            Pageable pageable) {
        return service.getAllLoansApplications(identifier, loanType, loanTerm, amount, minAmount, maxAmount, status, customerIdentity, pageable);
    }
    @GetMapping("/searchFor/{identity}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<LoanApplicationResponse> findLoanApplication(@PathVariable String identity){
        LoanApplicationResponse response = service.findLoanApplication(identity);
        return ResponseEntity.ok(response);
    }
    @PostMapping
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<String> saveNewLoanApplication(@RequestBody @Valid LoanApplicationRequest request) {
        service.createNewLoanApplication(request);
        return new ResponseEntity<>("Votre demande de crédit a été transmise avec succès. Vous serez informé par email pour obtenir les détails de votre demande",HttpStatus.CREATED);
    }
    @PostMapping("/approveLoanApplication/{identity}")
    @PreAuthorize("hasAuthority('AGENT')")
    public ResponseEntity<String> approveLoanApplication(@PathVariable String identity){
        service.approveLoanApplication(identity);
        return new ResponseEntity<>("Demande de crédit a été accepté avec succès",HttpStatus.ACCEPTED);
    }
    @PostMapping("/declineLoanApplication/{identity}")
    @PreAuthorize("hasAuthority('AGENT')")
    public ResponseEntity<String> rejectLoanApplication(@PathVariable String identity){
        service.declineLoanApplication(identity);
        return new ResponseEntity<>("Demande de crédit a été rejetée",HttpStatus.ACCEPTED);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('AGENT')")
    public ResponseEntity<LoanApplicationResponse> removeLoanApplicationFromDb(@PathVariable Long id){
        service.removeLoanApplication(id);
        return ResponseEntity.noContent().build();
    }
}
