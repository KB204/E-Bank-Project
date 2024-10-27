package net.banking.accountservice.service;

import net.banking.accountservice.dto.bankaccount.BankAccountResponse;
import net.banking.accountservice.dto.bankaccount.ChangeAccountStatus;
import net.banking.accountservice.dto.currentaccount.CurrentAccountRequest;
import net.banking.accountservice.dto.currentaccount.CurrentAccountResponse;
import net.banking.accountservice.dto.savingaccount.SavingAccountRequest;
import net.banking.accountservice.dto.savingaccount.SavingAccountResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BankAccountService {
    Page<BankAccountResponse> getAllBankAccounts(String rib, String branch, String accountStatus, String identity, Pageable pageable);
    Page<CurrentAccountResponse> getAllCurrentAccounts(String rib, String branch, String accountStatus, String identity, Pageable pageable);
    Page<SavingAccountResponse> getAllSavingAccounts(String rib, String branch, String accountStatus, String identity, Pageable pageable);
    void createNewCurrentAccount(CurrentAccountRequest request);
    void createNewSavingAccount(SavingAccountRequest request);
    void changeAccountStatus(String rib, ChangeAccountStatus request);
    void deleteBankAccount(Long id);
}
