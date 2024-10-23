package net.banking.accountservice.bankaccount;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
class BankAccountServiceImpl implements BankAccountService{
    private final BankAccountRepository bankAccountRepository;
    private final BankAccountMapper mapper;

    BankAccountServiceImpl(BankAccountRepository bankAccountRepository, BankAccountMapper mapper) {
        this.bankAccountRepository = bankAccountRepository;
        this.mapper = mapper;
    }
    @Override
    public List<BankAccountResponse> getAllBankAccounts() {
        return bankAccountRepository.findAll()
                .stream()
                .map(mapper::bankAccountToDtoResponse)
                .toList();
    }

    @Override
    public void createNewCurrentAccount() {

    }

    @Override
    public void createNewSavingAccount() {

    }
}
