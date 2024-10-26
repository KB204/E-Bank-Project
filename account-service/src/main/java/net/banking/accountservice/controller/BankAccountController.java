package net.banking.accountservice.controller;

import jakarta.validation.Valid;
import net.banking.accountservice.dto.bankaccount.BankAccountResponse;
import net.banking.accountservice.dto.currentaccount.CurrentAccountRequest;
import net.banking.accountservice.dto.currentaccount.CurrentAccountResponse;
import net.banking.accountservice.dto.savingaccount.SavingAccountRequest;
import net.banking.accountservice.dto.savingaccount.SavingAccountResponse;
import net.banking.accountservice.service.BankAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class BankAccountController {
    private final BankAccountService bankAccountService;
    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BankAccountResponse> findAllAccounts(){
        return bankAccountService.getAllBankAccounts();
    }
    @GetMapping("/allCurrentAccounts")
    @ResponseStatus(HttpStatus.OK)
    public List<CurrentAccountResponse> findAllCurrentAccounts() { return bankAccountService.getAllCurrentAccounts(); }
    @GetMapping("/allSavingAccounts")
    @ResponseStatus(HttpStatus.OK)
    public List<SavingAccountResponse> findAllSavingAccounts() { return bankAccountService.getAllSavingAccounts(); }
    @PostMapping("/newCurrentAccount")
    public ResponseEntity<String> saveNewCurrentAccount(@RequestBody @Valid CurrentAccountRequest request) {
        bankAccountService.createNewCurrentAccount(request);
        return new ResponseEntity<>(String.format("Compte a été créé avec succès pour le client identifié par [%s]",request.identity()),HttpStatus.CREATED);
    }
    @PostMapping("/newSavingAccount")
    public ResponseEntity<String> saveNewSavingAccount(@RequestBody @Valid SavingAccountRequest request) {
        bankAccountService.createNewSavingAccount(request);
        return new ResponseEntity<>(String.format("Compte a été créé avec succès pour le client identifié par [%s]",request.identity()),HttpStatus.CREATED);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<BankAccountResponse> removeBankAccount(@PathVariable Long id) {
        bankAccountService.deleteBankAccount(id);
        return ResponseEntity.noContent().build();
    }
}
