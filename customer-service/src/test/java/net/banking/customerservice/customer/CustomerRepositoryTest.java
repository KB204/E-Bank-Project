package net.banking.customerservice.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataMongoTest
@Testcontainers
class CustomerRepositoryTest {
    @Container
    static MongoDBContainer container = new MongoDBContainer("mongo:8.0");
    @Autowired
    CustomerRepository repository;
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", container::getReplicaSetUrl);
    }
    List<Customer> customers = new ArrayList<>();
    @BeforeEach
    void setUp() {
        repository.deleteAll();
        System.out.println("-------------------------------------------");
        customers = List.of(
                Customer.builder().firstname("karim").lastname("bammou").identity("Test").email("karim@gmail.com")
                        .birth(LocalDate.of(2002,11,28)).address("rabat").build(),
                Customer.builder().firstname("med").lastname("bammou").identity("Test1").email("med@gmail.com")
                        .birth(LocalDate.of(2000,11,28)).address("rabat").build(),
                Customer.builder().firstname("chrif").lastname("chrif").identity("Test2").email("chrif@gmail.com")
                        .birth(LocalDate.of(1999,1,1)).address("meknes").build()
        );
        repository.saveAll(customers);
        System.out.println("---------------------------------------------");
    }
    @Test
    public void connectionEstablishedTest(){
        assertThat(container.isCreated()).isTrue();
        assertThat(container.isRunning()).isTrue();
    }
    @Test
    void shouldFindCustomerByIdentityAndIgnoreTheCase() {
        String identity = "Test";
        Optional<Customer> customer = repository.findByIdentityIgnoreCase(identity);
        assertThat(customer).isPresent();
    }
    @Test
    void shouldNotFindCustomerByIdentityAndIgnoreTheCase() {
        String identity = "Test00200";
        Optional<Customer> customer = repository.findByIdentityIgnoreCase(identity);
        assertThat(customer).isEmpty();
    }
    @Test
    void shouldFindCustomerByEmailAndIgnoreTheCase() {
        String email = "karim@gmail.com";
        Optional<Customer> customer = repository.findByEmailIgnoreCase(email);
        assertThat(customer).isPresent();
    }
    @Test
    void shouldNotFindCustomerByEmailAndIgnoreTheCase() {
        String email = "TTTT";
        Optional<Customer> customer = repository.findByEmailIgnoreCase(email);
        assertThat(customer).isEmpty();
    }
}