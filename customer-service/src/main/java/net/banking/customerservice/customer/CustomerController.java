package net.banking.customerservice.customer;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
class CustomerController {
    private final CustomerService service;

    CustomerController(CustomerService service) {
        this.service = service;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<CustomerDtoResponse> findAllCustomer(){
        return service.getAllCustomers();
    }

    @GetMapping("/{identity}")
    @ResponseStatus(HttpStatus.OK)
    CustomerDtoResponse findCustomerByIdentity(@PathVariable String identity){
        return service.getCustomerByIdentity(identity);
    }

    @PostMapping
    ResponseEntity<String> saveCustomer(@RequestBody @Valid CustomerDtoRequest request){
        service.createNewCustomer(request);
        return new ResponseEntity<>(String.format("Le client identifié par l'identité [%s] a été créé avec succès.",request.identity()),HttpStatus.CREATED);
    }
}
