package net.banking.customerservice.customer;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
interface CustomerMapper {
    CustomerDtoResponse customerToDtoResponse(Customer customer);
    Customer dtoRequestTocustomer(CustomerDtoRequest request);
}
