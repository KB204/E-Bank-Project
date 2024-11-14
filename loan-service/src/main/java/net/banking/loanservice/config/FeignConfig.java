package net.banking.loanservice.config;

import feign.codec.ErrorDecoder;
import net.banking.loanservice.exceptions.CustomErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }
}
