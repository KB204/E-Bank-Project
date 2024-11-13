package net.banking.loanservice.dto;

import lombok.Builder;

@Builder
public record Customer(String firstname, String lastname, String identity, String email) {}
