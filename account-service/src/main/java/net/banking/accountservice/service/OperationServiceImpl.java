package net.banking.accountservice.service;

import net.banking.accountservice.client.CustomerRest;
import net.banking.accountservice.dto.Customer;
import net.banking.accountservice.dto.operation.OperationRequest;
import net.banking.accountservice.dto.operation.OperationResponse;
import net.banking.accountservice.enums.AccountStatus;
import net.banking.accountservice.enums.TransactionType;
import net.banking.accountservice.exceptions.BankAccountException;
import net.banking.accountservice.exceptions.ResourceNotFoundException;
import net.banking.accountservice.mapper.OperationMapper;
import net.banking.accountservice.model.BankAccountTransaction;
import net.banking.accountservice.model.CurrentAccount;
import net.banking.accountservice.repository.BankAccountRepository;
import net.banking.accountservice.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class OperationServiceImpl implements OperationService{
    private final TransactionRepository transactionRepository;
    private final BankAccountRepository bankAccountRepository;
    private final OperationMapper mapper;
    private final CustomerRest rest;

    public OperationServiceImpl(TransactionRepository transactionRepository, BankAccountRepository bankAccountRepository, OperationMapper mapper, CustomerRest rest) {
        this.transactionRepository = transactionRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.mapper = mapper;
        this.rest = rest;
    }

    @Override
    public List<OperationResponse> getAllOperations() {
        return transactionRepository.findAll()
                .stream()
                .map(mapper::operationToDtoResponse)
                .toList();
    }
    @Override
    public void transferOperation(OperationRequest request) {

        BankAccountTransaction transactionFrom = BankAccountTransaction.builder()
                .amount(request.amount())
                .transactionType(TransactionType.DEBIT)
                .currentAccount(CurrentAccount.builder().rib(request.ribFrom()).build())
                .customer(rest.findCustomer(request.senderIdentity()))
                .build();

        BankAccountTransaction transactionTo = BankAccountTransaction.builder()
                .amount(request.amount())
                .transactionType(TransactionType.CREDIT)
                .currentAccount(CurrentAccount.builder().rib(request.ribTo()).build())
                .customer(rest.findCustomer(request.receiverIdentity()))
                .build();

        String ribFrom = transactionFrom.getCurrentAccount().getRib();
        String ribTo = transactionTo.getCurrentAccount().getRib();
        Double amount = transactionFrom.getAmount();
        String customerSender = transactionFrom.getCustomer().identity();
        String customerReceiver = transactionTo.getCustomer().identity();

        Customer customer = rest.findCustomer(customerSender);
        Customer customer2 = rest.findCustomer(customerReceiver);
        CurrentAccount bankAccountFrom = bankAccountRepository.findByRib(ribFrom)
                .orElseThrow(() -> new ResourceNotFoundException("Compte n'existe pas "+ribFrom));
        CurrentAccount bankAccountTo = bankAccountRepository.findByRib(ribTo)
                .orElseThrow(() -> new ResourceNotFoundException("Compte n'existe pas "+ribTo));

        checkBusinessRules(bankAccountFrom,bankAccountTo,amount);

        bankAccountFrom.setBalance(bankAccountFrom.getBalance() - amount);
        bankAccountTo.setBalance(bankAccountTo.getBalance() + amount);

        transactionFrom.setCreatedAt(LocalDateTime.now());
        transactionFrom.setDescription("Virement en faveur de "+customerReceiver);
        transactionFrom.setCurrentAccount(bankAccountFrom);
        transactionFrom.setCustomer(customer);

        transactionTo.setCreatedAt(LocalDateTime.now());
        transactionTo.setDescription("Virement reçu de "+customerSender);
        transactionTo.setCurrentAccount(bankAccountTo);
        transactionTo.setCustomer(customer2);

        transactionRepository.save(transactionFrom);
        transactionRepository.save(transactionTo);
    }
    private void checkBusinessRules(CurrentAccount bankAccountFrom,CurrentAccount bankAccountTo,Double amount){
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
    }
}
