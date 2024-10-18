package net.banking.customerservice.customer;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

interface CustomerRepository extends MongoRepository<Customer,String> {
    Optional<Customer> findByIdentityIgnoreCase(String identity);
    Optional<Customer> findByEmailIgnoreCase(String email);
}
