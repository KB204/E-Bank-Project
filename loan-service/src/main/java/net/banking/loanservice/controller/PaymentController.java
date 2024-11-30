package net.banking.loanservice.controller;

import jakarta.validation.Valid;
import net.banking.loanservice.dto.loan.LoanDetailsDTO;
import net.banking.loanservice.dto.payment.ChangeStatusDTO;
import net.banking.loanservice.dto.payment.PaymentRequest;
import net.banking.loanservice.dto.payment.PaymentResponse;
import net.banking.loanservice.service.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('AGENT')")
    Page<PaymentResponse> findAllLoansPayments(
            @RequestParam(required = false) Double amount,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String date,
            Pageable pageable){
        return service.getAllPayments(amount, minAmount, maxAmount, status, date, pageable);
    }

    @PostMapping("/newPayment")
    ResponseEntity<String> newLoanPayment(@RequestBody @Valid PaymentRequest request){
        service.makeNewPayment(request);
        return new ResponseEntity<>(String.format("Paiement du montant %s MAD pour le crédit identifié par %s a été effectué avec succès",request.amount(),request.identifier()),HttpStatus.CREATED);
    }
    @GetMapping("/{identifier}/paymentHistory")
    @ResponseStatus(HttpStatus.OK)
    LoanDetailsDTO getLoanPaymentsHistory(
            @PathVariable String identifier,
            @RequestParam(required = false) Double amount,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String date,
            Pageable pageable) {
        return service.loanPaymentHistory(identifier, amount, status, date, pageable);
    }
    @PutMapping("{id}/changePaymentStatus")
    @PreAuthorize("hasAuthority('AGENT')")
    ResponseEntity<String> updateLoanPaymentStatus(@PathVariable Long id,@RequestBody @Valid ChangeStatusDTO request){
        service.changePaymentStatus(id, request);
        return new ResponseEntity<>("Paiment a été modifié avec succès",HttpStatus.ACCEPTED);
    }
}
