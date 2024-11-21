package net.banking.loanservice.service;

import net.banking.loanservice.dto.loan.LoanDetailsDTO;
import net.banking.loanservice.dto.payment.ChangeStatusDTO;
import net.banking.loanservice.dto.payment.PaymentRequest;
import net.banking.loanservice.dto.payment.PaymentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface PaymentService {
    void makeNewPayment(PaymentRequest request);
    Page<PaymentResponse> getAllPayments(Double amount, Double minAmount, Double maxAmount, String status, String date, Pageable pageable);
    LoanDetailsDTO loanPaymentHistory(String identifier,Double amount,String status, String date, Pageable pageable);
    void changePaymentStatus(Long id, ChangeStatusDTO statusDTO);
}
