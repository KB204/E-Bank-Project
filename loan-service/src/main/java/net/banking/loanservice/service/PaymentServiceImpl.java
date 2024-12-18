package net.banking.loanservice.service;

import net.banking.loanservice.client.BankAccountRestClient;
import net.banking.loanservice.dao.LoanRepository;
import net.banking.loanservice.dao.PaymentRepository;
import net.banking.loanservice.dto.loan.LoanDetailsDTO;
import net.banking.loanservice.dto.payment.ChangeStatusDTO;
import net.banking.loanservice.dto.payment.PaymentRequest;
import net.banking.loanservice.dto.payment.PaymentResponse;
import net.banking.loanservice.dto.payment.PaymentResponseDTO;
import net.banking.loanservice.entities.Loan;
import net.banking.loanservice.entities.Payment;
import net.banking.loanservice.enums.LoanStatus;
import net.banking.loanservice.enums.PaymentStatus;
import net.banking.loanservice.exceptions.PaymentException;
import net.banking.loanservice.exceptions.ResourceNotFoundException;
import net.banking.loanservice.mapper.PaymentMapper;
import net.banking.loanservice.service.specification.PaymentSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService{
    private final PaymentRepository paymentRepository;
    private final LoanRepository loanRepository;
    private final PaymentMapper mapper;
    private final BankAccountRestClient restClient;
    private final SendNotificationService notificationService;

    public PaymentServiceImpl(PaymentRepository paymentRepository, LoanRepository loanRepository, PaymentMapper mapper, BankAccountRestClient restClient, SendNotificationService notificationService) {
        this.paymentRepository = paymentRepository;
        this.loanRepository = loanRepository;
        this.mapper = mapper;
        this.restClient = restClient;
        this.notificationService = notificationService;
    }

    @Override
    public Page<PaymentResponse> getAllPayments(Double amount, Double minAmount, Double maxAmount, String status,
                                                String date, Pageable pageable) {

        Specification<Payment> specification = PaymentSpecification.filterWithoutConditions()
                .and(PaymentSpecification.amountEqual(amount))
                .and(PaymentSpecification.amountBetween(minAmount, maxAmount))
                .and(PaymentSpecification.statusLike(status))
                .and(PaymentSpecification.dateLike(date));
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("paymentDate").descending());
        return paymentRepository.findAll(specification,pageable)
                .map(mapper::paymentToDtoResponse);
    }

    @Override
    public LoanDetailsDTO loanPaymentHistory(String identifier,Double amount,String status, String date, Pageable pageable) {

        Loan loan = loanRepository.findByLoanApplication_Identifier(identifier)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Le Crédit identifié par %s n'existe pas",identifier)));

        Specification<Payment> specification = Specification.where(PaymentSpecification.identifierEqual(identifier))
                .and(PaymentSpecification.amountEqual(amount))
                .and(PaymentSpecification.statusLike(status))
                .and(PaymentSpecification.dateLike(date));
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("paymentDate").descending());

        Page<Payment> payments = paymentRepository.findAll(specification,pageable);
        List<PaymentResponseDTO> paymentsResponse = payments
                .stream()
                .map(mapper::paymentToDetailsResponse)
                .toList();

        return LoanDetailsDTO.builder()
                .identifier(identifier)
                .principleAmount(loan.getPrincipleAmount())
                .monthlyInstallment(loan.getMonthlyInstallment())
                .status(loan.getStatus())
                .bankAccountRib(loan.getBankAccountRib())
                .payments(paymentsResponse)
                .build();
    }

    @Override
    public void makeNewPayment(PaymentRequest request) {
        Loan loan = loanRepository.findByLoanApplication_Identifier(request.identifier())
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Crédit identifié par %s n'existe pas",request.identifier())));

        PaymentStatus status = request.amount() < loan.getMonthlyInstallment() ?
                PaymentStatus.FAILED  : PaymentStatus.SUCCESS;

        Payment payment = Payment.builder()
                .amountPaid(request.amount())
                .paymentDate(LocalDateTime.now())
                .status(status)
                .loan(loan)
                .build();

        checkBusinessRules(payment);
        paymentRepository.save(payment);

        if (payment.getStatus().equals(PaymentStatus.SUCCESS)){
            notificationService.debitAccountEvent(loan.getBankAccountRib(),payment.getAmountPaid());
            Double updateBalance = calculateRemainingAmount(loan);
            loan.setRemainingBalance(updateBalance);
            loanRepository.save(loan);
        }
    }
    @Override
    public void changePaymentStatus(Long id, ChangeStatusDTO statusDTO) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Paiement du crédit identifié par %s n'existe pas",id)));

        payment.setStatus(statusDTO.status());
        paymentRepository.save(payment);
    }

    private Double calculateRemainingAmount(Loan loan){
        double amount = loan.getPayments()
                .stream()
                .filter(payment -> payment.getStatus().equals(PaymentStatus.SUCCESS))
                .mapToDouble(Payment::getAmountPaid)
                .sum();
        return loan.getPrincipleAmount() - amount;
    }
    private void checkBusinessRules(Payment payment){
        if (payment.getLoan().getStatus().equals(LoanStatus.CLOSED))
            throw new PaymentException(String.format("Le Crédit identifié par %s est cloturé",payment.getLoan().getLoanApplication().getIdentifier()));
        if (restClient.getBankAccountBalance(payment.getLoan().getBankAccountRib()) < payment.getAmountPaid())
            throw new PaymentException("Le solde du compte n’est pas suffisant pour effectuer cette opération");
        if (restClient.getBankAccountStatus(payment.getLoan().getBankAccountRib()).equals("CLOSED"))
            throw new PaymentException(String.format("Le compte identifié par %s est clôturé",payment.getLoan().getBankAccountRib()));
        if (restClient.getBankAccountStatus(payment.getLoan().getBankAccountRib()).equals("BLOCKED"))
            throw new PaymentException(String.format("Le compte identifié par %s est bloqué",payment.getLoan().getBankAccountRib()));

    }
}
