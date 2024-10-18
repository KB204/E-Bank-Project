package net.banking.customerservice.customer;

import java.util.List;

record ErrorResponse(String message, List<String> details) {}
