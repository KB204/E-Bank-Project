package net.banking.loanservice.dto.payment;

import jakarta.validation.constraints.NotNull;
import net.banking.loanservice.enums.PaymentStatus;

public record ChangeStatusDTO(@NotNull(message = "La status est obligatoire") PaymentStatus status) {}
