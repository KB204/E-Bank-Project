package net.banking.accountservice.service;

import net.banking.accountservice.client.CustomerRest;
import net.banking.accountservice.dto.EmailDetails;
import net.banking.accountservice.dto.bankaccount.BankAccountDetails;
import net.banking.accountservice.dto.operation.OperationRequest;
import net.banking.accountservice.dto.operation.OperationResponse;
import net.banking.accountservice.dto.operation.TransactionDTO;
import net.banking.accountservice.dto.operation.WithdrawRequest;
import net.banking.accountservice.enums.AccountStatus;
import net.banking.accountservice.enums.TransactionType;
import net.banking.accountservice.exceptions.BankAccountException;
import net.banking.accountservice.exceptions.ResourceNotFoundException;
import net.banking.accountservice.mapper.OperationMapper;
import net.banking.accountservice.model.BankAccount;
import net.banking.accountservice.model.BankAccountTransaction;
import net.banking.accountservice.model.SavingAccount;
import net.banking.accountservice.repository.BankAccountRepository;
import net.banking.accountservice.repository.TransactionRepository;
import net.banking.accountservice.service.specification.OperationSpecification;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class OperationServiceImpl implements OperationService{
    private final TransactionRepository transactionRepository;
    private final BankAccountRepository bankAccountRepository;
    private final OperationMapper mapper;
    private final CustomerRest rest;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.email.name}")
    private String emailExchange;
    @Value("${rabbitmq.binding.email.name}")
    private String emailRoutingKey;

    public OperationServiceImpl(TransactionRepository transactionRepository, BankAccountRepository bankAccountRepository, OperationMapper mapper, CustomerRest rest, RabbitTemplate rabbitTemplate) {
        this.transactionRepository = transactionRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.mapper = mapper;
        this.rest = rest;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public Page<OperationResponse> getAllOperations(Double amount, Double minAmount, Double maxAmount ,String transactionType, String rib, String customerIdentity,
                                                    LocalDateTime startDate, LocalDateTime endDate, String createdAt, Pageable pageable) {
        Specification<BankAccountTransaction> specification = Specification.where(OperationSpecification.filterWithoutConditions())
                .and(OperationSpecification.amountEqual(amount))
                .and(OperationSpecification.amountBetween(minAmount,maxAmount))
                .and(OperationSpecification.transactionTypeEqual(transactionType))
                .and(OperationSpecification.ribEqual(rib))
                .and(OperationSpecification.customerEqual(customerIdentity))
                .and(OperationSpecification.transactionDateBetween(startDate,endDate))
                .and(OperationSpecification.transactionDateLike(createdAt));
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize() , Sort.by("createdAt").descending());
        return transactionRepository.findAll(specification,pageable)
                .map(operation -> {
                    operation.setCustomer(rest.getCustomerByIdentity(operation.getBankAccount().getCustomerIdentity()));
                    return mapper.operationToDtoResponse(operation);
                });
    }
    @Override
    public void transferOperation(String rib,OperationRequest request) {

        BankAccount bankAccountFrom = bankAccountRepository.findByRibAndCustomerIdentity(rib, request.senderIdentity())
                .orElseThrow(() -> new ResourceNotFoundException("Compte ou identifiant du client n'existe pas "+rib));
        BankAccount bankAccountTo = bankAccountRepository.findByRibAndCustomerIdentity(request.ribTo(), request.receiverIdentity())
                .orElseThrow(() -> new ResourceNotFoundException("Compte ou identifiant du client n'existe pas "+request.ribTo()));

        BankAccountTransaction transactionFrom = BankAccountTransaction.builder()
                .amount(request.amount())
                .transactionType(TransactionType.DEBIT)
                .bankAccount(bankAccountFrom)
                .motif(request.motif())
                .build();

        BankAccountTransaction transactionTo = BankAccountTransaction.builder()
                .amount(request.amount())
                .transactionType(TransactionType.CREDIT)
                .bankAccount(bankAccountTo)
                .build();

        Double amount = transactionFrom.getAmount();
        String motif = transactionFrom.getMotif();
        String customerSender = transactionFrom.getBankAccount().getCustomerIdentity();
        String customerSenderEmail = transactionFrom.getBankAccount().getCustomerEmail();
        String customerReceiver = transactionTo.getBankAccount().getCustomerIdentity();
        String customerReceiverEmail = transactionTo.getBankAccount().getCustomerEmail();

        checkBusinessRules(bankAccountFrom,bankAccountTo,amount);

        bankAccountFrom.setBalance(bankAccountFrom.getBalance() - amount);
        bankAccountTo.setBalance(bankAccountTo.getBalance() + amount);

        transactionFrom.setCreatedAt(LocalDateTime.now());
        transactionFrom.setDescription("Virement en faveur du client identifié par "+customerReceiver);
        transactionFrom.setMotif(motif);
        transactionFrom.setBankAccount(bankAccountFrom);

        transactionTo.setCreatedAt(LocalDateTime.now());
        transactionTo.setDescription("Virement reçu du client identifié par "+customerSender);
        transactionTo.setMotif(motif);
        transactionTo.setBankAccount(bankAccountTo);

        transactionRepository.save(transactionFrom);
        transactionRepository.save(transactionTo);
        rabbitTemplate.convertAndSend(emailExchange,
                emailRoutingKey,
                EmailDetails.builder()
                        .body(String.format("Vous avez reçu un virement de %s %s de la part du client identifié par %s",amount,bankAccountFrom.getCurrency(),bankAccountFrom.getCustomerIdentity()))
                        .to(customerReceiverEmail)
                        .subject("Virement Reçu Avec Succès")
                        .build());
        rabbitTemplate.convertAndSend(emailExchange,
                emailRoutingKey,
                EmailDetails.builder()
                        .body(String.format("Vous venez de demander un virement de votre compte %s vers le compte %s intitulé %s d'un montant de %s %s",bankAccountFrom.getRib(),bankAccountTo.getRib(),bankAccountTo.getCustomerIdentity(),amount,bankAccountFrom.getCurrency()))
                        .to(customerSenderEmail)
                        .subject("Votre Ordre de Virement")
                        .build());
    }

    @Override
    public void withdrawalOperation(WithdrawRequest request) {

        BankAccount bankAccount = bankAccountRepository.findByRibIgnoreCase(request.ribFrom())
                .orElseThrow(() -> new ResourceNotFoundException("Compte n'existe pas"));

        BankAccountTransaction transaction = BankAccountTransaction.builder()
                .amount(request.amount())
                .transactionType(TransactionType.DEBIT)
                .bankAccount(bankAccount)
                .build();

        Double amount = transaction.getAmount();
        checkBusinessRules(bankAccount,amount);

        bankAccount.setBalance(bankAccount.getBalance() - amount);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setDescription("Retrait du montant "+amount+bankAccount.getCurrency());
        transaction.setBankAccount(bankAccount);

        transactionRepository.save(transaction);
    }

    @Override
    public BankAccountDetails bankAccountHistory(String rib,Double amount,String transactionType,LocalDateTime startDate,
                                                 LocalDateTime endDate,Pageable pageable) {
        BankAccount bankAccount = bankAccountRepository.findByRibIgnoreCase(rib)
                .orElseThrow(() -> new ResourceNotFoundException("Compte n'existe pas"));

        Specification<BankAccountTransaction> specification = Specification.where(OperationSpecification.ribEqual(rib))
                .and(OperationSpecification.amountEqual(amount))
                .and(OperationSpecification.transactionTypeEqual(transactionType))
                .and(OperationSpecification.transactionDateBetween(startDate, endDate));
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createdAt").descending());

        Page<BankAccountTransaction> transactions = transactionRepository.findAll(specification,pageable);
        List<TransactionDTO> transactionDto = transactions
                .stream()
                .map(mapper::operationToTransactionDto)
                .toList();
        return BankAccountDetails.builder()
                .rib(bankAccount.getRib())
                .transaction(transactionDto)
                .build();
    }
    private void checkBusinessRules(BankAccount bankAccountFrom,BankAccount bankAccountTo,Double amount){
        if (bankAccountFrom.getAccountStatus().equals(AccountStatus.CLOSED))
            throw new BankAccountException(String.format("Le compte identifié par %s est clôturé",bankAccountFrom.getRib()));
        if (bankAccountFrom.getAccountStatus().equals(AccountStatus.BLOCKED))
            throw new BankAccountException(String.format("Le compte identifié par %s est bloqué",bankAccountFrom.getRib()));
        if (bankAccountTo.getAccountStatus().equals(AccountStatus.CLOSED))
            throw new BankAccountException(String.format("Le compte identifié par %s est clôturé",bankAccountTo.getRib()));
        if (bankAccountTo.getAccountStatus().equals(AccountStatus.BLOCKED))
            throw new BankAccountException(String.format("Le compte identifié par %s est bloqué",bankAccountTo.getRib()));
        if (bankAccountFrom.getBalance() < amount)
            throw new BankAccountException("Le solde du compte est inférieur au montant souhaité à envoyer");
        if (bankAccountFrom.getRib().equals(bankAccountTo.getRib()))
            throw new BankAccountException("Le compte source ne peut pas être le même que la destination");
        if (bankAccountFrom instanceof SavingAccount){
            throw new BankAccountException("Vous ne pouvez pas effectuer un virement a partir d'un compte epargne");
        }
        if (!Objects.equals(bankAccountFrom.getCurrency(), bankAccountTo.getCurrency())){
            throw new BankAccountException("Problème de devise, vous n'avez pas le droit d'effectuer cette action veuillez contacter votre banque");
        }
    }
    private void checkBusinessRules(BankAccount bankAccount,Double amount){
        if (bankAccount.getAccountStatus().equals(AccountStatus.CLOSED))
            throw new BankAccountException(String.format("Le compte identifié par %s est clôturé",bankAccount.getRib()));
        if (bankAccount.getAccountStatus().equals(AccountStatus.BLOCKED))
            throw new BankAccountException(String.format("Le compte identifié par %s est bloqué",bankAccount.getRib()));
        if (bankAccount.getBalance() < amount)
            throw new BankAccountException("Le solde du compte est inférieur au montant souhaité à envoyer");
    }
}
