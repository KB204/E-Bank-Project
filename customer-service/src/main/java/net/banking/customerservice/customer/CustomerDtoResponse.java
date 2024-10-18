package net.banking.customerservice.customer;

import java.time.LocalDate;

record CustomerDtoResponse(String id,String firstname, String lastname, String identity,
                           LocalDate birth, String email, String address) {}
