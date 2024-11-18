package net.banking.loanservice.client;

import net.banking.loanservice.config.FeignConfig;
import net.banking.loanservice.dto.external_services.BankAccount;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "ACCOUNT-SERVICE",configuration = FeignConfig.class)
public interface BankAccountRestClient {
    @GetMapping("/api/accounts/findBankAccount/{rib}/{identity}")
    BankAccount findBankAccount(@PathVariable String rib, @PathVariable String identity);
    @GetMapping("/api/accounts/bankAccountBalance/{rib}")
    Double getBankAccountBalance(@PathVariable String rib);
    @GetMapping("/bankAccountStatus/{rib}")
    String getBankAccountStatus(@PathVariable String rib);
}
