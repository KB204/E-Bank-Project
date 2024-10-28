package net.banking.accountservice.mapper;

import net.banking.accountservice.dto.currentaccount.CurrentAccountResponse;
import net.banking.accountservice.dto.savingaccount.SavingAccountResponse;
import net.banking.accountservice.model.BankAccount;
import net.banking.accountservice.dto.bankaccount.BankAccountResponse;
import net.banking.accountservice.model.CurrentAccount;
import net.banking.accountservice.model.SavingAccount;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface BankAccountMapper {
    BankAccountResponse bankAccountToDtoResponse(BankAccount bankAccount);
    CurrentAccountResponse currentAccountToDtoResponse(CurrentAccount currentAccount);
    SavingAccountResponse savingAccountToDtoResponse(SavingAccount savingAccount);
}
