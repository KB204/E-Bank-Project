package net.banking.loanservice.controller;

import jakarta.validation.Valid;
import net.banking.loanservice.dto.loan.LoanResponse;
import net.banking.loanservice.dto.secrured_loan.SecuredLoanResponse;
import net.banking.loanservice.dto.unsecured_loan.UnsecuredLoanRequest;
import net.banking.loanservice.dto.unsecured_loan.UnsecuredLoanResponse;
import net.banking.loanservice.service.LoanService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {
    private final LoanService service;

    public LoanController(LoanService service) {
        this.service = service;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    Page<LoanResponse> getAllLoans(
            @RequestParam(required = false) String identifier,
            @RequestParam(required = false) Double amount,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) LocalDate started,
            @RequestParam(required = false) LocalDate ended,
            @RequestParam(required = false) LocalDate start,
            @RequestParam(required = false) LocalDate end,
            Pageable pageable){
        return service.findAllLoans(identifier, amount, status, started, ended, start, end, pageable);}
    @GetMapping("/allSecuredLoans")
    @ResponseStatus(HttpStatus.OK)
    List<SecuredLoanResponse> getAllSecuredLoans(){
        return service.findAllSecuredLoans();
    }
    @GetMapping("/allUnsecuredLoans")
    @ResponseStatus(HttpStatus.OK)
    Page<UnsecuredLoanResponse> getAllUnsecuredLoans(
            @RequestParam(required = false) String identifier,
            @RequestParam(required = false) Double amount,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) LocalDate started,
            @RequestParam(required = false) LocalDate ended,
            @RequestParam(required = false) LocalDate start,
            @RequestParam(required = false) LocalDate end,
            Pageable pageable){
        return service.findAllUnsecuredLoans(identifier, amount, status, started, ended, start, end, pageable);}

    @PostMapping("/createUnsecuredLoan")
    ResponseEntity<String> saveNewUnsecuredLoan(@RequestBody @Valid UnsecuredLoanRequest request){
        service.createUnsecuredLoan(request);
        return new ResponseEntity<>("Crédit a été créé avec succès",HttpStatus.CREATED);
    }
}
