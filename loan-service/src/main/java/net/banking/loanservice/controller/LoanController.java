package net.banking.loanservice.controller;

import jakarta.validation.Valid;
import net.banking.loanservice.dto.loan.LoanResponse;
import net.banking.loanservice.dto.secrured_loan.SecuredLoanResponse;
import net.banking.loanservice.dto.unsecured_loan.UnsecuredLoanRequest;
import net.banking.loanservice.dto.unsecured_loan.UnsecuredLoanResponse;
import net.banking.loanservice.service.LoanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    List<LoanResponse> getAllLoans(){
        return service.findAllLoans();
    }
    @GetMapping("/allSecuredLoans")
    @ResponseStatus(HttpStatus.OK)
    List<SecuredLoanResponse> getAllSecuredLoans(){
        return service.findAllSecuredLoans();
    }
    @GetMapping("/allUnsecuredLoans")
    @ResponseStatus(HttpStatus.OK)
    List<UnsecuredLoanResponse> getAllUnsecuredLoans(){
        return service.findAllUnsecuredLoans();
    }

    @PostMapping("/createUnsecuredLoan")
    ResponseEntity<String> saveNewUnsecuredLoan(@RequestBody @Valid UnsecuredLoanRequest request){
        service.createUnsecuredLoan(request);
        return new ResponseEntity<>("Crédit a été créé avec succès",HttpStatus.CREATED);
    }
}
