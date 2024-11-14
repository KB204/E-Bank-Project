package net.banking.loanservice.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import net.banking.loanservice.config.FeignConfig;
import net.banking.loanservice.dto.Customer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "CUSTOMER-SERVICE",configuration = FeignConfig.class)
public interface CustomerRestClient {
    @CircuitBreaker(name = "customerService",fallbackMethod = "getDefaultCustomer")
    @Retry(name = "retryCustomerService")
    @GetMapping("/api/customers/{identity}")
    Customer fetchCustomerByIdentity(@PathVariable String identity);
    @GetMapping("/api/customers/{identity}")
    Customer findCustomerByIdentity(@PathVariable String identity);

    default Customer getDefaultCustomer(String identity,Exception e){
        return Customer.builder()
                .firstname("Client non trouvé")
                .lastname("Client non trouvé")
                .email("Client non trouvé")
                .identity(identity)
                .build();
    }
}
