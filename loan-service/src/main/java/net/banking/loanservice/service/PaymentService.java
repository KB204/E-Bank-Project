package net.banking.loanservice.service;

import net.banking.loanservice.dto.payment.ChangeStatusDTO;
import net.banking.loanservice.dto.payment.PaymentRequest;
import net.banking.loanservice.dto.payment.PaymentResponse;

import java.util.List;

public interface PaymentService {
    void makeNewPayment(PaymentRequest request);
    List<PaymentResponse> getAllPayments();
    void changePaymentStatus(Long id, ChangeStatusDTO statusDTO);
}
