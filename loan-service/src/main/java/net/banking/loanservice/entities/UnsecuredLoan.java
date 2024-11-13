package net.banking.loanservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@PrimaryKeyJoinColumn(name = "id")
@Entity
public class UnsecuredLoan extends Loan{}
