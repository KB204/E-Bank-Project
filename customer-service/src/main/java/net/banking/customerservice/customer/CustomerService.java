package net.banking.customerservice.customer;

import java.util.List;

interface CustomerService {
    List<CustomerDtoResponse> getAllCustomers();
    CustomerDtoResponse getCustomerByIdentity(String identity);
    void createNewCustomer(CustomerDtoRequest request);
}
