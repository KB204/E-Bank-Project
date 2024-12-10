package net.banking.customerservice.mapper;

import net.banking.customerservice.dto.CustomerDtoRequest;
import net.banking.customerservice.dto.CustomerDtoResponse;
import net.banking.customerservice.entites.Customer;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerDtoResponse customerToDtoResponse(Customer customer);
    Customer dtoRequestTocustomer(CustomerDtoRequest request);
}
