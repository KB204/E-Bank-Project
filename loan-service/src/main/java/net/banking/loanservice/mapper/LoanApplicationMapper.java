package net.banking.loanservice.mapper;

import net.banking.loanservice.dto.loan_application.LoanApplicationResponse;
import net.banking.loanservice.entities.LoanApplication;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoanApplicationMapper {
    LoanApplicationResponse loanApplicationToDtoResponse(LoanApplication loanApplication);
}
