package net.banking.customerservice.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class CustomerTestIntegration {
    @Autowired
    private TestRestTemplate rest;
    @Autowired
    private CustomerService customers;
    @Container
    static MongoDBContainer container = new MongoDBContainer("mongo:8.0");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", container::getReplicaSetUrl);
    }
    @BeforeEach
    void setUp() {
        customers.createNewCustomer(CustomerDtoRequest.builder().firstname("karim").lastname("karim").identity("Test").email("karim@gmail.com")
                        .address("Tanger").birth(LocalDate.of(2020,2,25)).build());
        customers.createNewCustomer(CustomerDtoRequest.builder().firstname("xx").lastname("xx").identity("xx").email("x@gmail.com")
                .address("Tanger").birth(LocalDate.of(2020,2,25)).build());
    }
    @Test
    public void connectionEstablishedTest(){
        assertThat(container.isCreated()).isTrue();
        assertThat(container.isRunning()).isTrue();
    }
    /*@Test
    void shouldGetAllCustomers() {
        ResponseEntity<CustomerDtoResponse[]> response = rest
                .exchange("/api/customers", HttpMethod.GET,null,CustomerDtoResponse[].class);
        List<CustomerDtoResponse> content = Arrays.asList(Objects.requireNonNull(response.getBody()));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(content.size()).isEqualTo(2);
        assertThat(content).usingRecursiveComparison().isEqualTo(customers);
    }*/
    @Test
    void shouldGetCustomerByIdentity() {
        String identity = "Test";
        ResponseEntity<CustomerDtoResponse> response = rest
                .exchange("/api/customers/{identity}",HttpMethod.GET,null,CustomerDtoResponse.class,identity);
        CustomerDtoResponse content = response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(content).isNotNull();
        assertThat(content.identity()).isEqualTo(identity);
    }
    @Test
    void shouldNotGetCustomerByIdentity() {
        String identity = "yo";
        ResponseEntity<CustomerDtoResponse> response = rest
                .exchange("/api/customers/{identity}",HttpMethod.GET,null,CustomerDtoResponse.class,identity);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
    @Test
    void shouldSaveNewCustomer() {
        CustomerDtoRequest request = CustomerDtoRequest.builder()
                .firstname("hamid").lastname("hamid").identity("M").email("ma@gmail.com").address("Rabat").birth(LocalDate.of(2020,2,25))
                .build();
        ResponseEntity<String> response = rest
                .exchange("/api/customers",HttpMethod.POST,new HttpEntity<>(request),String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo("Le client identifié par l'identité [M] a été créé avec succès");
    }
}
