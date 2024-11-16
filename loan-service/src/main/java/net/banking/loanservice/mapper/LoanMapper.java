package net.banking.loanservice.mapper;

import net.banking.loanservice.dto.loan.LoanResponse;
import net.banking.loanservice.dto.secrured_loan.SecuredLoanResponse;
import net.banking.loanservice.dto.unsecured_loan.UnsecuredLoanResponse;
import net.banking.loanservice.entities.Loan;
import net.banking.loanservice.entities.SecuredLoan;
import net.banking.loanservice.entities.UnsecuredLoan;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoanMapper {
    LoanResponse loanObjectToDtoResponse(Loan loan);
    SecuredLoanResponse securedLoanToDtoResponse(SecuredLoan loan);
    UnsecuredLoanResponse unsecuredLoanToDtoResponse(UnsecuredLoan loan);
}
