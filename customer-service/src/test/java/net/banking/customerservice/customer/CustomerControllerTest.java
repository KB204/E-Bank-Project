package net.banking.customerservice.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.List;


@WebMvcTest(CustomerController.class)
class CustomerControllerTest {
    @MockBean
    private CustomerService customerService;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;

    List<CustomerDtoResponse> customers;

    @BeforeEach
    void setUp() {
        this.customers = List.of(
                CustomerDtoResponse.builder().firstname("karim").lastname("bammou").identity("Test").email("karim@gmail.com")
                        .address("Tanger").birth(LocalDate.of(2020,2,25)).build(),
                CustomerDtoResponse.builder().firstname("xx").lastname("xx").identity("Testx").email("x@gmail.com")
                        .address("Tanger").birth(LocalDate.of(2020,2,25)).build()
        );
    }
    @Test
    void shouldFindCustomerByIdentity() throws Exception {
        String identity = "Test";
        Mockito.when(customerService.getCustomerByIdentity(identity)).thenReturn(customers.getFirst());
        mvc.perform(MockMvcRequestBuilders.get("/api/customers/{identity}",identity))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(customers.getFirst())));
    }
    @Test
    void shouldNotFindCustomerByInvalidIdentity() throws Exception {
        String identity = "oo";
        Mockito.when(customerService.getCustomerByIdentity(identity)).thenThrow(ResourceNotFoundException.class);
        mvc.perform(MockMvcRequestBuilders.get("/api/customers/{identity}",identity))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json("{\"message\":\"Erreurs fonctionnelles\",\"details\":[null]}"));
    }
    @Test
    void shouldCreateNewCustomer() throws Exception {
        CustomerDtoRequest customerDTO = CustomerDtoRequest.builder()
                .firstname("chrif").lastname("chrif").identity("TF630").email("chrif@gmail.com").address("Meknes")
                .birth(LocalDate.of(1975,1,1)).build();
        Mockito.doNothing().when(customerService).createNewCustomer(Mockito.any());
        mvc.perform(MockMvcRequestBuilders.post("/api/customers")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(mapper.writeValueAsString(customerDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("Le client identifié par l'identité [TF630] a été créé avec succès"));
    }
    @Test
    void shouldEditExitingCustomerByIdentity() throws Exception {
        String identity = "TF630";
        UpdateCustomerDto customer = UpdateCustomerDto.builder()
                .email("chrif@gmail.com").address("Meknes").build();
        Mockito.doNothing().when(customerService).updateExistingCustomer(Mockito.eq(identity),Mockito.any());
        mvc.perform(MockMvcRequestBuilders.put("/api/customers/{identity}",identity)
                     .contentType(MediaType.APPLICATION_JSON_VALUE)
                     .content(mapper.writeValueAsString(customer)))
                .andExpect(MockMvcResultMatchers.status().isAccepted())
                .andExpect(MockMvcResultMatchers.content().string("Le client identifié par l'identité [TF630] a été modifié avec succès"));
    }
    @Test
    void shouldRemoveCustomerByIdentity() throws Exception {
        String identity = "TF630";
        mvc.perform(MockMvcRequestBuilders.delete("/api/customers/{identity}",identity))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}