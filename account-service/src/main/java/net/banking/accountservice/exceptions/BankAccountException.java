package net.banking.accountservice.exceptions;

public class BankAccountException extends RuntimeException {
    public BankAccountException(String message) {
        super(message);
    }
}
