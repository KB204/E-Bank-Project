package net.banking.loanservice.exceptions;

import feign.Response;
import feign.codec.ErrorDecoder;

public class CustomErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String s, Response response) {
        return switch (response.status()) {
            case 404 -> new ResourceNotFoundException("Client n'existe pas");
            default -> new Exception("Un Problème est servenu réessayer plus tard");
        };
    }
}
