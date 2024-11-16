package net.banking.loanservice.exceptions;

public class BankAccountException extends RuntimeException {
    public BankAccountException(String message) {
        super(message);
    }
}
