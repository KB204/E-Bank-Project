package net.banking.accountservice.service;

import net.banking.accountservice.dto.EmailDetails;
import net.banking.accountservice.exceptions.BankAccountException;
import net.banking.accountservice.exceptions.InvalidOtpException;
import net.banking.accountservice.exceptions.ResourceNotFoundException;
import net.banking.accountservice.model.BankAccount;
import net.banking.accountservice.model.CodeVerification;
import net.banking.accountservice.repository.BankAccountRepository;
import net.banking.accountservice.repository.CodeVerificationRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@Transactional
public class CodeVerificationImpl implements CodeVerificationService {
    private final CodeVerificationRepository repository;
    private final BankAccountRepository bankAccountRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.email.name}")
    private String emailExchange;
    @Value("${rabbitmq.binding.email.name}")
    private String emailRoutingKey;

    public CodeVerificationImpl(CodeVerificationRepository repository, BankAccountRepository bankAccountRepository, RabbitTemplate rabbitTemplate) {
        this.repository = repository;
        this.bankAccountRepository = bankAccountRepository;
        this.rabbitTemplate = rabbitTemplate;
    }
    @Override
    public void sendOtpCode(String rib){
        BankAccount bankAccount = bankAccountRepository.findByRibIgnoreCase(rib)
                .orElseThrow(() -> new ResourceNotFoundException("Compte n'existe pas"));
        String email = bankAccount.getCustomerEmail();
        if (email == null){
            throw new BankAccountException("Vous avez un problème d'email,veuillez contacter votre banque");
        }
        int otp = otpGenerator();
        EmailDetails emailDetails = EmailDetails.builder()
                .to(email)
                .body(otp+" Est le code de confirmation pour effectuer votre opération, ce code expirera dans 10 minutes")
                .subject("Code de vérification")
                .build();
        CodeVerification codeVerification = CodeVerification.builder()
                .code(otp)
                .dateExpiration(LocalDateTime.now().plusMinutes(10))
                .bankAccount(bankAccount)
                .build();

        repository.save(codeVerification);
        rabbitTemplate.convertAndSend(emailExchange,emailRoutingKey,emailDetails);
    }
    @Override
    public void verifyOtpCode(String rib, Integer code) {
        CodeVerification codeVerification = repository.findByCodeAndBankAccount_Rib(code, rib)
                .orElseThrow(() -> new InvalidOtpException("Code de vérification invalid"));
        if (codeVerification.getDateExpiration().isBefore(LocalDateTime.now())){
            repository.deleteById(codeVerification.getId());
            throw new InvalidOtpException("Code de vérification est expiré");
        }
    }
    private Integer otpGenerator(){
        Random random = new Random();
        return random.nextInt(100_000,999_999);
    }
}
