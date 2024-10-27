package net.banking.accountservice.dto.bankaccount;

import jakarta.validation.constraints.NotNull;
import net.banking.accountservice.enums.AccountStatus;

public record ChangeAccountStatus(@NotNull(message = "La status de changement est obligatpore") AccountStatus status) {}
