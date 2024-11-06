package net.banking.accountservice.service;


public interface CodeVerificationService {
    void sendOtpCode(String rib);
    void verifyOtpCode(String rib, Integer code);
}
