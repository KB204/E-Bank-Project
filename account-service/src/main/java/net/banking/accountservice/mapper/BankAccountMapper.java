package net.banking.accountservice.mapper;

import net.banking.accountservice.dto.CurrentAccountResponse;
import net.banking.accountservice.dto.SavingAccountResponse;
import net.banking.accountservice.model.BankAccount;
import net.banking.accountservice.dto.BankAccountResponse;
import net.banking.accountservice.dto.Customer;
import net.banking.accountservice.model.CurrentAccount;
import net.banking.accountservice.model.SavingAccount;
import org.mapstruct.Mapper;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface BankAccountMapper {
    BankAccountResponse bankAccountToDtoResponse(BankAccount bankAccount);
    CurrentAccountResponse currentAccountToDtoResponse(CurrentAccount currentAccount);
    SavingAccountResponse savingAccountToDtoResponse(SavingAccount savingAccount);
    default List<Customer> map(Customer customer){
        return customer == null ? Collections.emptyList() : List.of(customer);
    }
}
