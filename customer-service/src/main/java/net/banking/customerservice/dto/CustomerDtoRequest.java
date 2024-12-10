package net.banking.customerservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
@Builder
public record CustomerDtoRequest(
        @NotEmpty(message = "Le prénom est obligatoire")
        String firstname,
        @NotEmpty(message = "Le Nom est obligatoire")
        String lastname,
        @NotEmpty(message = "CIN est obligatoire")
        String identity,
        @NotNull(message = "La date de naissance est obligatoire")
        LocalDate birth,
        @NotEmpty(message = "L'email est obligatoire")
        @Email(message = "Format d'email invalid")
        String email,
        @NotEmpty(message = "L'adresse est obligatoire")
        String address) {}
