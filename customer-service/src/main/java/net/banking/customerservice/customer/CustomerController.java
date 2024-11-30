package net.banking.customerservice.customer;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@PreAuthorize("hasAuthority('AGENT')")
class CustomerController {
    private final CustomerService service;

    CustomerController(CustomerService service) {
        this.service = service;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    Page<CustomerDtoResponse> findAllCustomer(Pageable pageable){
        return service.getAllCustomers(pageable);
    }

    @GetMapping("/{identity}")
    @ResponseStatus(HttpStatus.OK)
    CustomerDtoResponse findCustomerByIdentity(@PathVariable String identity){
        return service.getCustomerByIdentity(identity);
    }

    @PostMapping
    ResponseEntity<String> saveCustomer(@RequestBody @Valid CustomerDtoRequest request){
        service.createNewCustomer(request);
        return new ResponseEntity<>(String.format("Le client identifié par l'identité [%s] a été créé avec succès",request.identity()),HttpStatus.CREATED);
    }
    @PutMapping("/{identity}")
    ResponseEntity<String> editCustomer(@PathVariable String identity,@RequestBody @Valid UpdateCustomerDto request){
        service.updateExistingCustomer(identity, request);
        return new ResponseEntity<>(String.format("Le client identifié par l'identité [%s] a été modifié avec succès",identity),HttpStatus.ACCEPTED);
    }
    @DeleteMapping("/{identity}")
    ResponseEntity<CustomerDtoResponse> removeCustomer(@PathVariable String identity){
        service.deleteCustomerByIdentity(identity);
        return ResponseEntity.noContent().build();
    }
}
