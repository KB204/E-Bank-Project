package net.banking.accountservice.exceptions;

public class InvalidOtpException extends RuntimeException{
    public InvalidOtpException(String message){ super(message); }
}
