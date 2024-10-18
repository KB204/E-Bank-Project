package net.banking.customerservice.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

interface CustomerService {
    Page<CustomerDtoResponse> getAllCustomers(Pageable pageable);
    CustomerDtoResponse getCustomerByIdentity(String identity);
    void createNewCustomer(CustomerDtoRequest request);
    void updateExistingCustomer(String identity,UpdateCustomerDto request);
    void deleteCustomerByIdentity(String identity);
}
