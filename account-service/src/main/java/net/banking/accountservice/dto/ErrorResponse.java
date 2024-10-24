package net.banking.accountservice.dto;

import java.util.List;

public record ErrorResponse(String message, List<String> details) {
}
