package net.banking.loanservice.exceptions;

public class FileHandlingException extends RuntimeException {
    public FileHandlingException(String message) {
        super(message);
    }
}