package net.banking.customerservice.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
class CustomerServiceImpl implements CustomerService{
    private final CustomerRepository repository;
    private final CustomerMapper mapper;

    CustomerServiceImpl(CustomerRepository repository, CustomerMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }
    @Override
    public Page<CustomerDtoResponse> getAllCustomers(Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("lastname").ascending());
        return repository.findAll(pageable)
                .map(mapper::customerToDtoResponse);
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
    @Override
    public void updateExistingCustomer(String identity, UpdateCustomerDto request) {
        Customer customer = repository.findByIdentityIgnoreCase(identity)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé"));
        customer.setEmail(request.email());
        customer.setAddress(request.address());
        repository.save(customer);
    }
    @Override
    public void deleteCustomerByIdentity(String identity) {
        Customer customer = repository.findByIdentityIgnoreCase(identity)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé"));
        repository.delete(customer);
    }
}
