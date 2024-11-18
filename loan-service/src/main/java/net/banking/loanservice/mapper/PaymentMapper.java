package net.banking.loanservice.mapper;

import net.banking.loanservice.dto.payment.PaymentResponse;
import net.banking.loanservice.entities.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    PaymentResponse paymentToDtoResponse(Payment payment);
}
