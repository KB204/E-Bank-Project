package net.banking.accountservice.controller;

import jakarta.validation.Valid;
import net.banking.accountservice.dto.bankaccount.BankAccountResponse;
import net.banking.accountservice.dto.bankaccount.ChangeAccountStatus;
import net.banking.accountservice.dto.currentaccount.CurrentAccountRequest;
import net.banking.accountservice.dto.currentaccount.CurrentAccountResponse;
import net.banking.accountservice.dto.savingaccount.SavingAccountRequest;
import net.banking.accountservice.dto.savingaccount.SavingAccountResponse;
import net.banking.accountservice.service.BankAccountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/accounts")
public class BankAccountController {
    private final BankAccountService bankAccountService;
    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<BankAccountResponse> findAllAccounts(
            @RequestParam(required = false) String rib,
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) String accountStatus,
            @RequestParam(required = false) String identity,
            Pageable pageable) {
        return bankAccountService.getAllBankAccounts(rib, branch, accountStatus, identity, pageable); }
    @GetMapping("/allCurrentAccounts")
    @ResponseStatus(HttpStatus.OK)
    public Page<CurrentAccountResponse> findAllCurrentAccounts(
            @RequestParam(required = false) String rib,
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) String accountStatus,
            @RequestParam(required = false) String identity,
            Pageable pageable) {
        return bankAccountService.getAllCurrentAccounts(rib, branch, accountStatus, identity, pageable); }
    @GetMapping("/allSavingAccounts")
    @ResponseStatus(HttpStatus.OK)
    public Page<SavingAccountResponse> findAllSavingAccounts(
            @RequestParam(required = false) String rib,
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) String accountStatus,
            @RequestParam(required = false) String identity,
            Pageable pageable) {
        return bankAccountService.getAllSavingAccounts(rib, branch, accountStatus, identity, pageable); }
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
    @PostMapping("/{rib}/changeStatus")
    public ResponseEntity<String> changeAccountStatusTo(@PathVariable String rib, @RequestBody @Valid ChangeAccountStatus request) {
        bankAccountService.changeAccountStatus(rib, request);
        return new ResponseEntity<>(String.format("Compte identifié par le rib [%s] a été modifié avec succès",rib),HttpStatus.ACCEPTED);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<BankAccountResponse> removeBankAccount(@PathVariable Long id) {
        bankAccountService.deleteBankAccount(id);
        return ResponseEntity.noContent().build();
    }
}