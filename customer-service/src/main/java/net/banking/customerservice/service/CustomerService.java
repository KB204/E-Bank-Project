package net.banking.customerservice.service;

import net.banking.customerservice.dto.UpdateCustomerDto;
import net.banking.customerservice.dto.CustomerDtoRequest;
import net.banking.customerservice.dto.CustomerDtoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerService {
    Page<CustomerDtoResponse> getAllCustomers(Pageable pageable);
    CustomerDtoResponse getCustomerByIdentity(String identity);
    void createNewCustomer(CustomerDtoRequest request);
    void updateExistingCustomer(String identity, UpdateCustomerDto request);
    void deleteCustomerByIdentity(String identity);
}
