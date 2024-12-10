package net.banking.customerservice.dto;

import java.util.List;

public record ErrorResponse(String message, List<String> details) {}
