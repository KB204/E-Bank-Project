package net.banking.accountservice.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import net.banking.accountservice.config.FeignConfig;
import net.banking.accountservice.dto.Customer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "CUSTOMER-SERVICE",configuration = FeignConfig.class)
public interface CustomerRest {
    @CircuitBreaker(name = "customerService",fallbackMethod = "getDefaultCustomer")
    @Retry(name = "retryCustomerService")
    @GetMapping("/api/customers/{identity}")
    Customer getCustomerByIdentity(@PathVariable String identity);
    @GetMapping("/api/customers/{identity}")
    Customer findCustomer(@PathVariable String identity);

    default Customer getDefaultCustomer(String identity,Exception e){
        return Customer.builder()
                .firstname("Client non trouvé")
                .lastname("Client non trouvé")
                .email("Client non trouvé")
                .identity(identity)
                .build();
    }
}
