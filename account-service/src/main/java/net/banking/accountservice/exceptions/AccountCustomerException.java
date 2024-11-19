package net.banking.accountservice.exceptions;

public class AccountCustomerException extends RuntimeException {
    public AccountCustomerException(String message) {
        super(message);
    }
}
