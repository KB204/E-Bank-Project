package net.banking.customerservice.customer;

import net.banking.customerservice.dao.CustomerRepository;
import net.banking.customerservice.dto.CustomerDtoRequest;
import net.banking.customerservice.dto.CustomerDtoResponse;
import net.banking.customerservice.dto.UpdateCustomerDto;
import net.banking.customerservice.entites.Customer;
import net.banking.customerservice.exceptions.ResourceAlreadyExists;
import net.banking.customerservice.exceptions.ResourceNotFoundException;
import net.banking.customerservice.mapper.CustomerMapper;
import net.banking.customerservice.service.CustomerServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    @Mock
    private CustomerRepository repository;
    @Mock
    private CustomerMapper mapper;
    @InjectMocks
    private CustomerServiceImpl underTest;

    @Test
    void shouldCreateNewCustomer() {
        CustomerDtoRequest customerDTO = CustomerDtoRequest.builder()
                .firstname("user").lastname("user").identity("Test").email("user@gmail.com").address("Tanger")
                .birth(LocalDate.of(2020,2,25))
                .build();
        Customer customer = Customer.builder()
                .firstname("user").lastname("user").identity("Test").email("user@gmail.com").address("Tanger")
                .birth(LocalDate.of(2020,2,25))
                .build();
        Mockito.when(repository.findByIdentityIgnoreCase(customerDTO.identity())).thenReturn(Optional.empty());
        Mockito.when(repository.findByEmailIgnoreCase(customerDTO.email())).thenReturn(Optional.empty());
        Mockito.when(mapper.dtoRequestTocustomer(customerDTO)).thenReturn(customer);
        Mockito.when(repository.save(customer)).thenReturn(customer);

        underTest.createNewCustomer(customerDTO);
        assertThat(customerDTO).isNotNull();
    }
    @Test
    void shouldNotCreateNewCustomerWhenExistingIdentity(){
        CustomerDtoRequest customerDTO = CustomerDtoRequest.builder()
                .firstname("user").lastname("user").identity("Test").email("user@gmail.com").address("Tanger")
                .birth(LocalDate.of(2020,2,25))
                .build();
        Customer customer = Customer.builder()
                .firstname("user").lastname("user").identity("Test").email("user@gmail.com").address("Tanger")
                .birth(LocalDate.of(2020,2,25))
                .build();
        Mockito.when(repository.findByIdentityIgnoreCase(customerDTO.identity())).thenReturn(Optional.of(customer));
        assertThatThrownBy(() -> underTest.createNewCustomer(customerDTO))
                .isInstanceOf(ResourceAlreadyExists.class);
    }
    @Test
    void shouldNotCreateNewCustomerWhenExistingEmail(){
        CustomerDtoRequest customerDTO = CustomerDtoRequest.builder()
                .firstname("user").lastname("user").identity("Test").email("user@gmail.com").address("Tanger")
                .birth(LocalDate.of(2020,2,25))
                .build();
        Customer customer = Customer.builder()
                .firstname("user").lastname("user").identity("Test").email("user@gmail.com").address("Tanger")
                .birth(LocalDate.of(2020,2,25))
                .build();
        Mockito.when(repository.findByEmailIgnoreCase(customerDTO.email())).thenReturn(Optional.of(customer));
        assertThatThrownBy(() -> underTest.createNewCustomer(customerDTO))
                .isInstanceOf(ResourceAlreadyExists.class);
    }
    /*@Test
    void shouldGetAllCustomers() {
        List<Customer> customers = List.of(
                Customer.builder().firstname("user").lastname("user").identity("Test").email("user@gmail.com").address("Tanger")
                        .birth(LocalDate.of(2020,2,25)).build()
        );
        List<CustomerDtoResponse> responses = List.of(
                CustomerDtoResponse.builder().firstname("user").lastname("user").identity("Test").email("user@gmail.com").address("Tanger")
                        .birth(LocalDate.of(2020,2,25)).build()
        );
        Mockito.when(repository.findAll()).thenReturn(customers);
        Mockito.when(mapper.fromCustomersToDtoResponse(customers)).thenReturn(responses);
        Pageable pageable = PageRequest.of(0,10);
        Page<CustomerDtoResponse> result = underTest.getAllCustomers(pageable);
        assertThat(responses).usingRecursiveComparison().isEqualTo(result);
    }*/

    @Test
    void shouldFindCustomerByIdentity() {
        String identity = "RG45";
        Customer customer = Customer.builder().firstname("user").lastname("user").identity("Test").email("user@gmail.com").address("Tanger")
                .birth(LocalDate.of(2020,2,25)).build();
        CustomerDtoResponse customerDTO = CustomerDtoResponse.builder().firstname("user").lastname("user").identity("Test").email("user@gmail.com").address("Tanger")
                .birth(LocalDate.of(2020,2,25)).build();
        Mockito.when(repository.findByIdentityIgnoreCase(identity)).thenReturn(Optional.of(customer));
        Mockito.when(mapper.customerToDtoResponse(customer)).thenReturn(customerDTO);

        CustomerDtoResponse response = underTest.getCustomerByIdentity(identity);
        assertThat(response).isNotNull();
        assertThat(customerDTO).usingRecursiveComparison().isEqualTo(response);
    }
    @Test
    void shouldNotFindCustomerByIdentity(){
        String identity = "RG45";
        Mockito.when(repository.findByIdentityIgnoreCase(identity)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> underTest.getCustomerByIdentity(identity))
                .isInstanceOf(ResourceNotFoundException.class);
    }
    @Test
    void shouldUpdateExistingCustomerByIdentity() {
        String identity = "RG45";
        UpdateCustomerDto customerDTO = UpdateCustomerDto.builder()
                .email("user@gmail.com").address("Tanger")
                .build();
        Customer customer = Customer.builder()
                .email("user@gmail.com").address("Tanger")
                .build();
        Mockito.when(repository.findByIdentityIgnoreCase(identity)).thenReturn(Optional.of(customer));
        Mockito.when(repository.save(customer)).thenReturn(customer);

        underTest.updateExistingCustomer(identity,customerDTO);
        assertThat(customerDTO).isNotNull();
    }
    @Test
    void shouldDeleteCustomerByIdentity() {
        String identity = "TT";
        Customer customer = Customer.builder()
                .firstname("user").lastname("user").identity("Test").email("user@gmail.com").address("Tanger")
                .birth(LocalDate.of(2020,2,25))
                .build();
        Mockito.when(repository.findByIdentityIgnoreCase(identity)).thenReturn(Optional.of(customer));

        underTest.deleteCustomerByIdentity(identity);
        Mockito.verify(repository).delete(customer);
    }
    @Test
    void shouldNotDeleteCustomerByIdentity(){
        String identity = "TT";
        Mockito.when(repository.findByIdentityIgnoreCase(identity)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> underTest.deleteCustomerByIdentity(identity))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}