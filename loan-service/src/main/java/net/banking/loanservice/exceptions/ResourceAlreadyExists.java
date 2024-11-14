package net.banking.loanservice.exceptions;

public class ResourceAlreadyExists extends RuntimeException {
    public ResourceAlreadyExists(String message) {
        super(message);
    }
}
