package net.banking.customerservice.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record UpdateCustomerDto(
        @NotEmpty(message = "L'email est obligatoire")
        @Email(message = "Format d'email invalid")
        String email,
        @NotEmpty(message = "L'adresse est obligatoire")
        String address) {}
