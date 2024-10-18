package net.banking.customerservice.customer;

class ResourceNotFoundException extends RuntimeException{
    ResourceNotFoundException(String message) {
        super(message);
    }
}
