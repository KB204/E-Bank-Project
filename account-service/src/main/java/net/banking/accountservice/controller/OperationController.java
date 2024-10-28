package net.banking.accountservice.controller;

import jakarta.validation.Valid;
import net.banking.accountservice.dto.operation.OperationRequest;
import net.banking.accountservice.dto.operation.OperationResponse;
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
            @RequestParam(required = false) Double amount2,
            @RequestParam(required = false) String transactionType,
            @RequestParam(required = false) String rib,
            @RequestParam(required = false) String customerIdentity,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(required = false) String createdAt,
            Pageable pageable) {
        return service.getAllOperations(amount, amount2, transactionType, rib, customerIdentity, startDate, endDate, createdAt, pageable);
    }

    @PostMapping("/newPayment")
    public ResponseEntity<String> makeNewOperation(@RequestBody @Valid OperationRequest request) {
        service.transferOperation(request);
        return new ResponseEntity<>(String.format("Virement du montant %s en faveur de %s par %s a été effectué avec succès",request.amount(),request.ribTo(),request.ribFrom()),HttpStatus.CREATED);
    }
}
