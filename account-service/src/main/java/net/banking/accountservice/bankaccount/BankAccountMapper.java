package net.banking.accountservice.bankaccount;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
interface BankAccountMapper {
    BankAccountResponse bankAccountToDtoResponse(BankAccount bankAccount);
    CurrentAccountResponse currentAccountToDtoResponse(CurrentAccount currentAccount);
    BankAccount DtoToCurrentAccount(CurrentAccountRequest currentAccountRequest);
    SavingAccountResponse savingAccountToDtoResponse(SavingAccount savingAccount);
    BankAccount DtoToSavingAccount(SavingAccountRequest savingAccountRequest);
}
