package net.banking.customerservice.customer;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
class CustomerServiceImpl implements CustomerService{
    private final CustomerRepository repository;
    private final CustomerMapper mapper;

    CustomerServiceImpl(CustomerRepository repository, CustomerMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }
    @Override
    public List<CustomerDtoResponse> getAllCustomers() {
        List<Customer> customers = repository.findAll();
        return customers.stream()
                .map(mapper::customerToDtoResponse)
                .toList();
    }
    @Override
    public CustomerDtoResponse getCustomerByIdentity(String identity) {
        Customer customer = repository.findByIdentityIgnoreCase(identity)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé"));
        return mapper.customerToDtoResponse(customer);
    }
    @Override
    public void createNewCustomer(CustomerDtoRequest request) {
        Customer customer = mapper.dtoRequestTocustomer(request);
        repository.findByIdentityIgnoreCase(request.identity())
                .ifPresent(a -> {
                    throw new ResourceAlreadyExists("Client exists déja");
                });
        repository.findByEmailIgnoreCase(request.email())
                .ifPresent(a -> {
                    throw new ResourceAlreadyExists("Client exists déja");
                });
        repository.save(customer);
    }
}
