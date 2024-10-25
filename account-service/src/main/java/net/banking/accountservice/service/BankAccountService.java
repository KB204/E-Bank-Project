package net.banking.accountservice.service;

import net.banking.accountservice.dto.*;

import java.util.List;

public interface BankAccountService {
    List<BankAccountResponse> getAllBankAccounts();
    List<CurrentAccountResponse> getAllCurrentAccounts();
    List<SavingAccountResponse> getAllSavingAccounts();
    void createNewCurrentAccount(CurrentAccountRequest request);
    void createNewSavingAccount(SavingAccountRequest request);
    void deleteBankAccount(Long id);
}
