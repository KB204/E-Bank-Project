package net.banking.customerservice.customer;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
interface CustomerMapper {
    CustomerDtoResponse customerToDtoResponse(Customer customer);
    Customer dtoRequestTocustomer(CustomerDtoRequest request);
    //List<CustomerDtoResponse> fromCustomersToDtoResponse(List<Customer> customer);
}
