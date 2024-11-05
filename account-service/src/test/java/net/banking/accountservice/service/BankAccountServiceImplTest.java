package net.banking.accountservice.service;

import net.banking.accountservice.client.CustomerRest;
import net.banking.accountservice.dto.Customer;
import net.banking.accountservice.dto.currentaccount.CurrentAccountRequest;
import net.banking.accountservice.exceptions.ResourceAlreadyExists;
import net.banking.accountservice.model.CurrentAccount;
import net.banking.accountservice.repository.BankAccountRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceImplTest {
    @Mock
    private BankAccountRepository repository;
    @Mock
    private CustomerRest rest;
    @InjectMocks
    private BankAccountServiceImpl underTest;

    @Test
    void shouldCreateNewCurrentAccountWhenCustomerExistsAndRibIsUnique() {
        // given
        Customer customer = Customer.builder().identity("RG45").email("chrif@gmail.com")
                .lastname("chrif").firstname("chrif").build();

        CurrentAccountRequest request = new CurrentAccountRequest(500.0,"MAD","RABAT","RG45");
        // when
        Mockito.when(rest.findCustomer(request.identity())).thenReturn(customer);
        Mockito.when(repository.findByRibIgnoreCase(Mockito.anyString())).thenReturn(Optional.empty());

        underTest.createNewCurrentAccount(request);
        // then
        ArgumentCaptor<CurrentAccount> captorAccount = ArgumentCaptor.forClass(CurrentAccount.class);
        Mockito.verify(repository).save(captorAccount.capture());
        CurrentAccount savedAccount = captorAccount.getValue();

        assertThat(savedAccount).isNotNull();
        assertThat(savedAccount.getBalance()).isEqualTo(request.balance());
        assertThat(savedAccount.getCurrency()).isEqualTo(request.currency());
        assertThat(savedAccount.getBranch()).isEqualTo(request.branch());
        assertThat(savedAccount.getCustomerIdentity()).isEqualTo(request.identity());
    }
    @Test
    void shouldNotCreateNewCurrentAccount() {
        // given
        Customer customer = Customer.builder().identity("RG45").email("chrif@gmail.com")
                .lastname("chrif").firstname("chrif").build();
        CurrentAccountRequest request = new CurrentAccountRequest(500.0,"MAD","RABAT","RG45");
        CurrentAccount currentAccount = new CurrentAccount();
        // when
        Mockito.when(rest.findCustomer(request.identity())).thenReturn(customer);
        Mockito.when(repository.findByRibIgnoreCase(Mockito.anyString())).thenReturn(Optional.of(currentAccount));
        // then
        assertThatThrownBy(() -> underTest.createNewCurrentAccount(request))
                .isInstanceOf(ResourceAlreadyExists.class)
                .hasMessage("Compte déjà existant avec ce RIB");
        Mockito.verify(repository,Mockito.never()).save(Mockito.any(CurrentAccount.class));
    }

    @Test
    @Disabled
    void changeAccountStatus() {
    }

    @Test
    @Disabled
    void deleteBankAccount() {
    }
}