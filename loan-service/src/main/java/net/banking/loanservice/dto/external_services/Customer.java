package net.banking.loanservice.dto.external_services;

import lombok.Builder;

@Builder
public record Customer(String firstname, String lastname, String identity, String email) {}
