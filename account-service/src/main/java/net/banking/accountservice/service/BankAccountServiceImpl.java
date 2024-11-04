package net.banking.accountservice.service;

import net.banking.accountservice.client.CustomerRest;
import net.banking.accountservice.dto.*;
import net.banking.accountservice.dto.bankaccount.BankAccountResponse;
import net.banking.accountservice.dto.bankaccount.ChangeAccountStatus;
import net.banking.accountservice.dto.currentaccount.CurrentAccountRequest;
import net.banking.accountservice.dto.currentaccount.CurrentAccountResponse;
import net.banking.accountservice.dto.savingaccount.SavingAccountRequest;
import net.banking.accountservice.dto.savingaccount.SavingAccountResponse;
import net.banking.accountservice.enums.AccountStatus;
import net.banking.accountservice.exceptions.ResourceAlreadyExists;
import net.banking.accountservice.exceptions.ResourceNotFoundException;
import net.banking.accountservice.mapper.BankAccountMapper;
import net.banking.accountservice.model.*;
import net.banking.accountservice.repository.BankAccountRepository;
import net.banking.accountservice.service.specification.BankAccountSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    public Page<BankAccountResponse> getAllBankAccounts(String rib, String branch, String accountStatus,
                                                        String identity, Pageable pageable) {
        Specification<BankAccount> specification = Specification.where(BankAccountSpecification.filterWithoutConditions())
                .and(BankAccountSpecification.ribEqual(rib))
                .and(BankAccountSpecification.branchLike(branch))
                .and(BankAccountSpecification.statusEqual(accountStatus))
                .and(BankAccountSpecification.customerIdentityEqual(identity));
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createdAt").descending());
        return bankAccountRepository.findAll(specification,pageable)
                .map(bankAccount -> {
                    bankAccount.setCustomer(rest.getCustomerByIdentity(bankAccount.getCustomerIdentity()));
                    return mapper.bankAccountToDtoResponse(bankAccount);
                });
    }
    @Override
    public Page<CurrentAccountResponse> getAllCurrentAccounts(String rib, String branch, String accountStatus,
                                                              String identity, Pageable pageable) {
        Specification<BankAccount> specification = Specification.where(BankAccountSpecification.currentAccountsOnly())
                .and(BankAccountSpecification.ribEqual(rib))
                .and(BankAccountSpecification.branchLike(branch))
                .and(BankAccountSpecification.statusEqual(accountStatus))
                .and(BankAccountSpecification.customerIdentityEqual(identity));
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createdAt").descending());
        return bankAccountRepository.findAll(specification,pageable)
                .map(bankAccount -> {
                    CurrentAccount currentAccount = (CurrentAccount) bankAccount;
                    currentAccount.setCustomer(rest.getCustomerByIdentity(currentAccount.getCustomerIdentity()));
                    return mapper.currentAccountToDtoResponse(currentAccount);
                });
    }

    @Override
    public Page<SavingAccountResponse> getAllSavingAccounts(String rib, String branch, String accountStatus,
                                                            String identity, Pageable pageable) {
        Specification<BankAccount> specification = Specification.where(BankAccountSpecification.savingAccountOnly())
                .and(BankAccountSpecification.ribEqual(rib))
                .and(BankAccountSpecification.branchLike(branch))
                .and(BankAccountSpecification.statusEqual(accountStatus))
                .and(BankAccountSpecification.customerIdentityEqual(identity));
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createdAt").descending());
        return bankAccountRepository.findAll(specification,pageable)
                .map(bankAccount -> {
                    SavingAccount savingAccount = (SavingAccount) bankAccount;
                    savingAccount.setCustomer(rest.getCustomerByIdentity(savingAccount.getCustomerIdentity()));
                    return mapper.savingAccountToDtoResponse(savingAccount);
                });
    }

    @Override
    public void createNewCurrentAccount(CurrentAccountRequest request) {
            Customer customer = rest.findCustomer(request.identity());
            CurrentAccount currentAccount = CurrentAccount.builder()
                    .rib(UUID.randomUUID().toString().substring(0,8))
                    .createdAt(LocalDateTime.now())
                    .accountStatus(AccountStatus.OPENED)
                    .customerIdentity(customer.identity())
                    .customerEmail(customer.email())
                    .balance(request.balance())
                    .currency(request.currency())
                    .branch(request.branch())
                    .overDraftLimit(1000.0)
                    .overDraftFees(0.5)
                    .build();
            bankAccountRepository.findByRibIgnoreCase(currentAccount.getRib())
                    .ifPresent(bankAccount -> {
                        throw new ResourceAlreadyExists("Compte déjà existant avec ce RIB");
                    });
            bankAccountRepository.save(currentAccount);
    }
    @Override
    public void createNewSavingAccount(SavingAccountRequest request) {
        Customer customer = rest.findCustomer(request.identity());
        SavingAccount savingAccount = SavingAccount.builder()
                .rib(UUID.randomUUID().toString().substring(0,8))
                .createdAt(LocalDateTime.now())
                .accountStatus(AccountStatus.OPENED)
                .customerIdentity(customer.identity())
                .customerEmail(customer.email())
                .balance(request.balance())
                .currency(request.currency())
                .branch(request.branch())
                .interest(1.5)
                .withDrawLimit(5000.0)
                .build();
        bankAccountRepository.findByRibIgnoreCase(savingAccount.getRib())
                .ifPresent(bankAccount -> {
                    throw new ResourceAlreadyExists("Compte déjà existant avec ce RIB");
                });
        bankAccountRepository.save(savingAccount);
    }
    public void changeAccountStatus(String rib, ChangeAccountStatus request){
        BankAccount bankAccount = bankAccountRepository.findByRibIgnoreCase(rib)
                .orElseThrow(() -> new ResourceNotFoundException("Compte n'existe pas"));
        bankAccount.setAccountStatus(request.status());
        bankAccountRepository.save(bankAccount);
    }
    @Override
    public void deleteBankAccount(Long id) {
        BankAccount bankAccount = bankAccountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compte n'existe pas"));
        bankAccountRepository.delete(bankAccount);
    }
}
