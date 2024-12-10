package net.banking.customerservice.dto;

import lombok.Builder;

import java.time.LocalDate;
@Builder
public record CustomerDtoResponse(String id,String firstname, String lastname, String identity,
                           LocalDate birth, String email, String address) {}
