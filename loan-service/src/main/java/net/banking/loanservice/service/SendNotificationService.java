package net.banking.loanservice.service;

public interface SendNotificationService {
    void loanPendingNotification(String identifier);
    void loanApprovedNotification(String identifier);
    void loanDeclinedNotification(String identifier);
    void debitAccountEvent(String rib,Double amount);
}
