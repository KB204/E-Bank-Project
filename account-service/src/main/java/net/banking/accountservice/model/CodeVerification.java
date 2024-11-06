package net.banking.accountservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
public class CodeVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Integer code;
    private LocalDateTime dateExpiration;
    @ManyToOne(fetch = FetchType.LAZY)
    private BankAccount bankAccount;
}
