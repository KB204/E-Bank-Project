package net.banking.accountservice.service;

import net.banking.accountservice.client.CustomerRest;
import net.banking.accountservice.dto.*;
import net.banking.accountservice.enums.AccountStatus;
import net.banking.accountservice.exceptions.ResourceAlreadyExists;
import net.banking.accountservice.exceptions.ResourceNotFoundException;
import net.banking.accountservice.mapper.BankAccountMapper;
import net.banking.accountservice.model.*;
import net.banking.accountservice.repository.BankAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class BankAccountServiceImpl implements BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final BankAccountMapper mapper;
    private final CustomerRest rest;

    BankAccountServiceImpl(BankAccountRepository bankAccountRepository, BankAccountMapper mapper, CustomerRest rest) {
        this.bankAccountRepository = bankAccountRepository;
        this.mapper = mapper;
        this.rest = rest;
    }
    @Override
    public List<BankAccountResponse> getAllBankAccounts() {
        return bankAccountRepository.findAll()
                .stream()
                .map(bankAccount -> {
                    bankAccount.setCustomer(rest.getCustomerByIdentity(bankAccount.getCustomerIdentity()));
                    return mapper.bankAccountToDtoResponse(bankAccount);
                })
                .toList();
    }
    @Override
    public List<CurrentAccountResponse> getAllCurrentAccounts() {
        return bankAccountRepository.findAll()
                .stream()
                .filter(bankAccount -> bankAccount instanceof CurrentAccount)
                .map(bankAccount -> {
                    CurrentAccount currentAccount = (CurrentAccount) bankAccount;
                    currentAccount.setCustomer(rest.getCustomerByIdentity(currentAccount.getCustomerIdentity()));
                    return mapper.currentAccountToDtoResponse(currentAccount);
                })
                .toList();
    }

    @Override
    public List<SavingAccountResponse> getAllSavingAccounts() {
        return bankAccountRepository.findAll()
                .stream()
                .filter(bankAccount -> bankAccount instanceof SavingAccount)
                .map(bankAccount -> {
                    SavingAccount savingAccount = (SavingAccount) bankAccount;
                    savingAccount.setCustomer(rest.getCustomerByIdentity(savingAccount.getCustomerIdentity()));
                    return mapper.savingAccountToDtoResponse(savingAccount);
                })
                .toList();
    }

    @Override
    public void createNewCurrentAccount(CurrentAccountRequest request) {
            Customer customer = rest.getCustomerByIdentity(request.identity());
            CurrentAccount currentAccount = CurrentAccount.builder()
                    .rib(UUID.randomUUID().toString().substring(0,8))
                    .createdAt(LocalDateTime.now())
                    .accountStatus(AccountStatus.OPENED)
                    .customerIdentity(customer.identity())
                    .balance(request.balance())
                    .currency(request.currency())
                    .branch(request.branch())
                    .overDraftLimit(1000.0)
                    .overDraftFees(0.5)
                    .build();
            bankAccountRepository.findByRibIgnoreCase(currentAccount.getRib())
                    .ifPresent(bankAccount -> {
                        throw new ResourceAlreadyExists("Compte exits déja");
                    });
            bankAccountRepository.save(currentAccount);
    }
    @Override
    public void createNewSavingAccount(SavingAccountRequest request) {
        Customer customer = rest.getCustomerByIdentity(request.identity());
        SavingAccount savingAccount = SavingAccount.builder()
                .rib(UUID.randomUUID().toString().substring(0,8))
                .createdAt(LocalDateTime.now())
                .accountStatus(AccountStatus.OPENED)
                .customerIdentity(customer.identity())
                .balance(request.balance())
                .currency(request.currency())
                .branch(request.branch())
                .interest(1.5)
                .withDrawLimit(5000.0)
                .build();
        bankAccountRepository.findByRibIgnoreCase(savingAccount.getRib())
                .ifPresent(bankAccount -> {
                    throw new ResourceAlreadyExists("Compte exits déja");
                });
        bankAccountRepository.save(savingAccount);
    }
    @Override
    public void deleteBankAccount(Long id) {
        BankAccount bankAccount = bankAccountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compte n'existe pas"));
        bankAccountRepository.delete(bankAccount);
    }
}
