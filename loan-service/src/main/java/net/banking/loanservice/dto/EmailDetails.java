package net.banking.loanservice.dto;

import lombok.Builder;

@Builder
public record EmailDetails(String to,String subject,String body) {
}
