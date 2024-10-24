package net.banking.accountservice.service;

import net.banking.accountservice.dto.BankAccountResponse;
import net.banking.accountservice.dto.CurrentAccountRequest;
import net.banking.accountservice.dto.SavingAccountRequest;

import java.util.List;

public interface BankAccountService {
    List<BankAccountResponse> getAllBankAccounts();
    void createNewCurrentAccount(CurrentAccountRequest request);
    void createNewSavingAccount(SavingAccountRequest request);
    void deleteBankAccount(Long id);
}
