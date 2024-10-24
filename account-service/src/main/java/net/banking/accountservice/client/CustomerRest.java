package net.banking.accountservice.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import net.banking.accountservice.dto.Customer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

@FeignClient(value = "customer",url = "http://localhost:8081")
public interface CustomerRest {
    @CircuitBreaker(name = "customerService",fallbackMethod = "getDefaultCustomer")
    @Retry(name = "customerService")
    @GetMapping("/api/customers/{identity}")
    @ResponseStatus(HttpStatus.OK)
    Customer getCustomerByIdentity(@PathVariable String identity);

    default Customer getDefaultCustomer(String identity,Exception e){
        return Customer.builder()
                .firstname("Client non trouvé")
                .lastname("Client non trouvé")
                .identity("Client non trouvé")
                .build();
    }
}
