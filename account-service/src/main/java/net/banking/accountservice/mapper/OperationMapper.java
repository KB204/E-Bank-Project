package net.banking.accountservice.mapper;

import net.banking.accountservice.dto.operation.OperationResponse;
import net.banking.accountservice.model.BankAccountTransaction;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface OperationMapper {
    OperationResponse operationToDtoResponse(BankAccountTransaction bankAccountTransaction);
}
