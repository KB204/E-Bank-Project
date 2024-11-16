package net.banking.accountservice.service;


import net.banking.accountservice.model.BankAccount;

public interface CodeVerificationService {
    void sendOtpCode(String rib);
    void verifyOtpCode(String rib, Integer code);
    void sendNotificationEmail(BankAccount from, BankAccount to, Double amount);
}
