package net.banking.customerservice.dao;

import net.banking.customerservice.entites.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CustomerRepository extends MongoRepository<Customer,String> {
    Optional<Customer> findByIdentityIgnoreCase(String identity);
    Optional<Customer> findByEmailIgnoreCase(String email);
}
