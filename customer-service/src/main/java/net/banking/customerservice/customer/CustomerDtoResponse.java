package net.banking.customerservice.customer;

import lombok.Builder;

import java.time.LocalDate;
@Builder
record CustomerDtoResponse(String id,String firstname, String lastname, String identity,
                           LocalDate birth, String email, String address) {}
