package net.banking.accountservice.service;

import net.banking.accountservice.client.CustomerRest;
import net.banking.accountservice.dto.bankaccount.BankAccountDetails;
import net.banking.accountservice.dto.operation.*;
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
    private final CodeVerificationService codeVerificationService;
    private final OperationMapper mapper;
    private final CustomerRest rest;

    public OperationServiceImpl(TransactionRepository transactionRepository, BankAccountRepository bankAccountRepository, CodeVerificationService codeVerificationService, OperationMapper mapper, CustomerRest rest) {
        this.transactionRepository = transactionRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.codeVerificationService = codeVerificationService;
        this.mapper = mapper;
        this.rest = rest;
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
    public void transferOperation(String rib, OperationRequest request) {

        BankAccount bankAccountFrom = findBankAccount(rib,request.senderIdentity());
        BankAccount bankAccountTo = findBankAccount(request.ribTo(), request.receiverIdentity());

        checkBusinessRules(bankAccountFrom,bankAccountTo, request.amount());
        codeVerificationService.sendOtpCode(rib);
    }
    @Override
    public void completeTransferOperation(String rib, CompleteOperationDTO request) {

        BankAccount bankAccountFrom = findBankAccount(rib,request.senderIdentity());
        BankAccount bankAccountTo = findBankAccount(request.ribTo(), request.receiverIdentity());

        codeVerificationService.verifyOtpCode(rib, request.otp());
        performTransfer(bankAccountFrom, bankAccountTo, request);
    }
    @Override
    public void withdrawalOperation(String rib, WithdrawRequest request) {
        BankAccount bankAccount = bankAccountRepository.findByRibIgnoreCase(rib)
                .orElseThrow(() -> new ResourceNotFoundException("Compte n'existe pas"));

        checkBusinessRules(bankAccount,request.amount());
        codeVerificationService.sendOtpCode(rib);
    }
    @Override
    public void completeWithdrawalOperation(String rib, CompleteWithdrawDTO request) {
        BankAccount bankAccount = bankAccountRepository.findByRibIgnoreCase(rib)
                .orElseThrow(() -> new ResourceNotFoundException("Compte n'existe pas"));

        codeVerificationService.verifyOtpCode(rib, request.otp());
        performWithdrawal(bankAccount,request);
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
    private BankAccount findBankAccount(String rib, String identity) {
        return bankAccountRepository.findByRibAndCustomerIdentity(rib, identity)
                .orElseThrow(() -> new ResourceNotFoundException("Compte ou identifiant du client n'existe pas " + rib));
    }
    private void performTransfer(BankAccount from, BankAccount to, CompleteOperationDTO request) {
        Double amount = request.amount();
        String motif = request.motif();

        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);

        transactionRepository.save(createTransaction(from, amount, TransactionType.DEBIT, "Virement en faveur du client identifié par " + to.getCustomerIdentity(), motif));
        transactionRepository.save(createTransaction(to, amount, TransactionType.CREDIT, "Virement reçu du client identifié par " + from.getCustomerIdentity(), motif));

        codeVerificationService.sendNotificationEmail(from, to, amount);
    }
    private void performWithdrawal(BankAccount bankAccount, CompleteWithdrawDTO request){
        Double amount = request.amount();
        bankAccount.setBalance(bankAccount.getBalance() - amount);

        transactionRepository.save(createTransaction(bankAccount,amount, "Retrait du montant "+amount+bankAccount.getCurrency()));
    }
    private BankAccountTransaction createTransaction(BankAccount account, Double amount, TransactionType type, String description, String motif) {
        return BankAccountTransaction.builder()
                .amount(amount)
                .transactionType(type)
                .bankAccount(account)
                .description(description)
                .motif(motif)
                .createdAt(LocalDateTime.now())
                .build();
    }
    private BankAccountTransaction createTransaction(BankAccount account, Double amount, String description) {
        return BankAccountTransaction.builder()
                .amount(amount)
                .transactionType(TransactionType.DEBIT)
                .bankAccount(account)
                .description(description)
                .createdAt(LocalDateTime.now())
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
        if (bankAccountFrom instanceof SavingAccount)
            throw new BankAccountException("Vous ne pouvez pas effectuer un virement a partir d'un compte epargne");
        if (amount > 6000.0)
            throw new BankAccountException("Vous avez dépassé le plafond autorisé");
        if (!Objects.equals(bankAccountFrom.getCurrency(), bankAccountTo.getCurrency()))
            throw new BankAccountException("Problème de devise, vous n'avez pas le droit d'effectuer cette action veuillez contacter votre banque");
    }
    private void checkBusinessRules(BankAccount bankAccount,Double amount){
        if (bankAccount.getAccountStatus().equals(AccountStatus.CLOSED))
            throw new BankAccountException(String.format("Le compte identifié par %s est clôturé",bankAccount.getRib()));
        if (bankAccount.getAccountStatus().equals(AccountStatus.BLOCKED))
            throw new BankAccountException(String.format("Le compte identifié par %s est bloqué",bankAccount.getRib()));
        if (bankAccount.getBalance() < amount)
            throw new BankAccountException("Le solde du compte est inférieur au montant souhaité à envoyer");
        if (amount > 6000.0)
            throw new BankAccountException("Vous avez dépassé le plafond autorisé");
    }
}
