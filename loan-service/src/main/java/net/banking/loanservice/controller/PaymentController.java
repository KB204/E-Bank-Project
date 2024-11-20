package net.banking.loanservice.controller;

import jakarta.validation.Valid;
import net.banking.loanservice.dto.loan.LoanDetailsDTO;
import net.banking.loanservice.dto.payment.ChangeStatusDTO;
import net.banking.loanservice.dto.payment.PaymentRequest;
import net.banking.loanservice.dto.payment.PaymentResponse;
import net.banking.loanservice.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<PaymentResponse> findAllLoansPayments(){
        return service.getAllPayments();
    }

    @PostMapping("/newPayment")
    ResponseEntity<String> newLoanPayment(@RequestBody @Valid PaymentRequest request){
        service.makeNewPayment(request);
        return new ResponseEntity<>(String.format("Paiement du montant %s MAD pour le crédit identifié par %s a été effectué avec succès",request.amount(),request.identifier()),HttpStatus.CREATED);
    }
    @GetMapping("/{identifier}/paymentHistory")
    @ResponseStatus(HttpStatus.OK)
    LoanDetailsDTO getLoanPaymentsHistory(@PathVariable String identifier) {
        return service.loanPaymentHistory(identifier);
    }
    @PutMapping("{id}/changePaymentStatus")
    ResponseEntity<String> updateLoanPaymentStatus(@PathVariable Long id,@RequestBody @Valid ChangeStatusDTO request){
        service.changePaymentStatus(id, request);
        return new ResponseEntity<>("Paiment a été modifié avec succès",HttpStatus.ACCEPTED);
    }
}
