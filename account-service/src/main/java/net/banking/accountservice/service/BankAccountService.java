package net.banking.accountservice.service;

import net.banking.accountservice.dto.bankaccount.BankAccountResponse;
import net.banking.accountservice.dto.currentaccount.CurrentAccountRequest;
import net.banking.accountservice.dto.currentaccount.CurrentAccountResponse;
import net.banking.accountservice.dto.savingaccount.SavingAccountRequest;
import net.banking.accountservice.dto.savingaccount.SavingAccountResponse;

import java.util.List;

public interface BankAccountService {
    List<BankAccountResponse> getAllBankAccounts();
    List<CurrentAccountResponse> getAllCurrentAccounts();
    List<SavingAccountResponse> getAllSavingAccounts();
    void createNewCurrentAccount(CurrentAccountRequest request);
    void createNewSavingAccount(SavingAccountRequest request);
    void deleteBankAccount(Long id);
}
