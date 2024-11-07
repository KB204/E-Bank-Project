package net.banking.accountservice.controller;

import jakarta.validation.Valid;
import net.banking.accountservice.dto.bankaccount.BankAccountDetails;
import net.banking.accountservice.dto.operation.*;
import net.banking.accountservice.service.OperationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
@RestController
@RequestMapping("/api/operations")
public class OperationController {
    private final OperationService service;

    public OperationController(OperationService service) {
        this.service = service;
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<OperationResponse> findAllOperations(
            @RequestParam(required = false) Double amount,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount,
            @RequestParam(required = false) String transactionType,
            @RequestParam(required = false) String rib,
            @RequestParam(required = false) String customerIdentity,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(required = false) String createdAt,
            Pageable pageable) {
        return service.getAllOperations(amount, minAmount, maxAmount,transactionType, rib, customerIdentity, startDate, endDate, createdAt, pageable);
    }
    @GetMapping("/{rib}/details")
    @ResponseStatus(HttpStatus.OK)
    public BankAccountDetails bankAccountDetails(
            @PathVariable String rib,
            @RequestParam(required = false) Double amount,
            @RequestParam(required = false) String transactionType,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            Pageable pageable){
        return service.bankAccountHistory(rib, amount, transactionType, startDate, endDate, pageable); }
    @PostMapping("/newPayment")
    public ResponseEntity<String> makeNewOperation(@RequestParam String rib,@RequestBody @Valid OperationRequest request) {
        service.transferOperation(rib, request);
        return new ResponseEntity<>("Le code de vérification a été envoyé à votre adresse e-mail",HttpStatus.CREATED);
    }
    @PostMapping("/completeNewPayment")
    public ResponseEntity<String> completeNewTransferOperation(@RequestParam String rib,@RequestBody @Valid CompleteOperationDTO request) {
        service.completeTransferOperation(rib, request);
        return new ResponseEntity<>(String.format("Virement du montant %s en faveur de %s par %s a été effectué avec succès",request.amount(),request.ribTo(),rib),HttpStatus.CREATED);
    }
    @PostMapping("/newWithdrawal")
    public ResponseEntity<String> makeNewWithdrawal(@RequestParam String rib,@RequestBody @Valid WithdrawRequest request) {
        service.withdrawalOperation(rib, request);
        return new ResponseEntity<>("Le code de vérification a été envoyé à votre adresse e-mail",HttpStatus.CREATED);
    }
    @PostMapping("/completeNewWithdraw")
    public ResponseEntity<String> completeNewWithdrawalOperation(@RequestParam String rib, @RequestBody @Valid CompleteWithdrawDTO request) {
        service.completeWithdrawalOperation(rib, request);
        return new ResponseEntity<>(String.format("Retrait du montant %s a été effectué avec succès",request.amount()),HttpStatus.CREATED);
    }
}
