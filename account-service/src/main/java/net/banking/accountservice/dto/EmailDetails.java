package net.banking.accountservice.dto;

import lombok.Builder;

@Builder
public record EmailDetails(String to,String subject,String body) {
}
