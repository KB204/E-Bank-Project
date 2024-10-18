package net.banking.customerservice.customer;

class ResourceAlreadyExists extends RuntimeException{
    ResourceAlreadyExists(String message) {
        super(message);
    }
}
