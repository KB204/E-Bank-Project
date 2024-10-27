package net.banking.accountservice.controller;

import jakarta.validation.Valid;
import net.banking.accountservice.dto.operation.OperationRequest;
import net.banking.accountservice.dto.operation.OperationResponse;
import net.banking.accountservice.service.OperationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/operations")
public class OperationController {
    private final OperationService service;

    public OperationController(OperationService service) {
        this.service = service;
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<OperationResponse> findAllOperations() { return service.getAllOperations(); }

    @PostMapping("/newPayment")
    public ResponseEntity<String> makeNewOperation(@RequestBody @Valid OperationRequest request) {
        service.transferOperation(request);
        return new ResponseEntity<>(String.format("Virement du montant %s en faveur de %s par %s a été effectué avec succès",request.amount(),request.ribTo(),request.ribFrom()),HttpStatus.CREATED);
    }
}
