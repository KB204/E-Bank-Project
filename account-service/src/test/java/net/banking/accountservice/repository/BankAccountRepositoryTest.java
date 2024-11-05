package net.banking.accountservice.repository;

import net.banking.accountservice.enums.AccountStatus;
import net.banking.accountservice.model.BankAccount;
import net.banking.accountservice.model.CurrentAccount;
import net.banking.accountservice.model.SavingAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BankAccountRepositoryTest {
    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16");
    @Autowired
    BankAccountRepository repository;

    List<CurrentAccount> currentAccounts = new ArrayList<>();
    List<SavingAccount> savingAccounts = new ArrayList<>();

    @BeforeEach
    void setUp() {
        System.out.println("-----------------------------------------");
        currentAccounts = List.of(
                CurrentAccount.builder().rib("test").balance(500.0).branch("RABAT").accountStatus(AccountStatus.OPENED)
                        .createdAt(LocalDateTime.now()).currency("MAD").customerIdentity("RG45").customerEmail("chrif@gmail.com")
                        .overDraftFees(1.5).overDraftLimit(1000.0).build(),
                CurrentAccount.builder().rib("test1").balance(550.0).branch("RABAT").accountStatus(AccountStatus.OPENED)
                        .createdAt(LocalDateTime.now()).currency("MAD").customerIdentity("TT99").customerEmail("med@gmail.com")
                        .overDraftFees(1.5).overDraftLimit(1000.0).build()
        );
        savingAccounts = List.of(
                SavingAccount.builder().rib("test2").balance(3500.0).branch("RABAT").accountStatus(AccountStatus.OPENED)
                        .createdAt(LocalDateTime.now()).currency("MAD").customerIdentity("RG45").customerEmail("chrif@gmail.com")
                        .interest(2.5).withDrawLimit(9000.0).build(),
                SavingAccount.builder().rib("test3").balance(3000.0).branch("RABAT").accountStatus(AccountStatus.OPENED)
                        .createdAt(LocalDateTime.now()).currency("MAD").customerIdentity("TT99").customerEmail("Med@gmail.com")
                        .interest(2.5).withDrawLimit(9000.0).build()
        );
        repository.saveAll(currentAccounts);
        repository.saveAll(savingAccounts);
        System.out.println("------------------------------------------");
    }

    @Test
    public void connectionEstablishedTest(){
        assertThat(postgreSQLContainer.isCreated()).isTrue();
        assertThat(postgreSQLContainer.isRunning()).isTrue();
    }

    @Test
    void shouldFindBankAccountByRibIgnoreCase() {
        String givenRib = "test";
        Optional<BankAccount> bankAccount = repository.findByRibIgnoreCase(givenRib);
        assertThat(bankAccount).isPresent();
    }
    @Test
    void shouldNotFindBankAccountByRib() {
        String givenRib = "mm0";
        Optional<BankAccount> bankAccount = repository.findByRibIgnoreCase(givenRib);
        assertThat(bankAccount).isEmpty();
    }
    @Test
    void shouldFindByRibAndCustomerIdentity() {
        String givenRib = "test1";
        String givenCustomer = "TT99";
        Optional<BankAccount> bankAccount = repository.findByRibAndCustomerIdentity(givenRib,givenCustomer);
        assertThat(bankAccount).isPresent();
    }
    @Test
    void shouldNotFindByRibAndCustomer() {
        String givenRib = "xxx";
        String givenCustomer = "Rfx";
        Optional<BankAccount> bankAccount = repository.findByRibAndCustomerIdentity(givenRib,givenCustomer);
        assertThat(bankAccount).isEmpty();
    }
}