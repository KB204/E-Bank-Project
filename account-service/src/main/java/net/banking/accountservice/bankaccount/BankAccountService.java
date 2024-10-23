package net.banking.accountservice.bankaccount;

import java.util.List;

interface BankAccountService {
    List<BankAccountResponse> getAllBankAccounts();
    void createNewCurrentAccount();
    void createNewSavingAccount();
}
