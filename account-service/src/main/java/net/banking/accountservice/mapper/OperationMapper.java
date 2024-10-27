package net.banking.accountservice.mapper;

import net.banking.accountservice.dto.Customer;
import net.banking.accountservice.dto.operation.OperationResponse;
import net.banking.accountservice.model.BankAccountTransaction;
import org.mapstruct.Mapper;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OperationMapper {
    OperationResponse operationToDtoResponse(BankAccountTransaction bankAccountTransaction);
    default List<Customer> map(Customer customer){
        return customer == null ? Collections.emptyList() : List.of(customer);
    }
}
