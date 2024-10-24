package net.banking.accountservice.mapper;

import net.banking.accountservice.model.BankAccount;
import net.banking.accountservice.dto.BankAccountResponse;
import net.banking.accountservice.dto.Customer;
import org.mapstruct.Mapper;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface BankAccountMapper {
    BankAccountResponse bankAccountToDtoResponse(BankAccount bankAccount);
    default List<Customer> map(Customer customer){
        return customer == null ? Collections.emptyList() : List.of(customer);
    }
}
