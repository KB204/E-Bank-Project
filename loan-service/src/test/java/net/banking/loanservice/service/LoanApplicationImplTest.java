package net.banking.loanservice.service;

import net.banking.loanservice.client.CustomerRestClient;
import net.banking.loanservice.dao.LoanApplicationRepository;
import net.banking.loanservice.dto.external_services.Customer;
import net.banking.loanservice.dto.loan_application.LoanApplicationRequest;
import net.banking.loanservice.entities.LoanApplication;
import net.banking.loanservice.enums.LoanType;
import net.banking.loanservice.exceptions.ResourceAlreadyExists;
import net.banking.loanservice.mapper.LoanApplicationMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanApplicationImplTest {
    @Mock
    private LoanApplicationRepository repository;
    @Mock
    private SendNotificationService sendNotificationService;
    @Mock
    private LoanApplicationMapper mapper;
    @Mock
    private CustomerRestClient restClient;
    @InjectMocks
    private LoanApplicationImpl underTest;

    /*@Test
    void shouldGetAllLoansApplications() {
        // given
        LoanApplication loanApplication1 = LoanApplication.builder()
                .identifier("test")
                .loanType(LoanType.AUTO)
                .loanTerm(30)
                .requestedAmount(95000.0)
                .customerIdentity("TP")
                .build();

        LoanApplication loanApplication2 = LoanApplication.builder()
                .identifier("test2")
                .loanType(LoanType.BUSINESS)
                .loanTerm(10)
                .requestedAmount(63000.0)
                .customerIdentity("TP")
                .build();

        List<LoanApplication> loanApplications = List.of(loanApplication1, loanApplication2);

        Customer customer = Customer.builder().identity("TP").email("test@gmail.com").build();

        LoanApplicationResponse response1 = LoanApplicationResponse.builder()
                .identifier("test")
                .loanType(LoanType.AUTO)
                .loanTerm(30)
                .requestedAmount(95000.0)
                .build();

        LoanApplicationResponse response2 = LoanApplicationResponse.builder()
                .identifier("test2")
                .loanType(LoanType.BUSINESS)
                .loanTerm(10)
                .requestedAmount(63000.0)
                .build();

        List<LoanApplicationResponse> expectedResponses = List.of(response1, response2);

        // when
        when(repository.findAll()).thenReturn(loanApplications);
        when(restClient.fetchCustomerByIdentity("TP")).thenReturn(customer);
        when(mapper.loanApplicationToDtoResponse(loanApplication1)).thenReturn(response1);
        when(mapper.loanApplicationToDtoResponse(loanApplication2)).thenReturn(response2);
        List<LoanApplicationResponse> responses = underTest.getAllLoansApplications();

        // then
        assertEquals(expectedResponses.size(),responses.size());
        assertEquals(expectedResponses.getFirst().identifier(), responses.getFirst().identifier());
        assertEquals(expectedResponses.get(1).identifier(), responses.get(1).identifier());
        verify(restClient,times(2)).fetchCustomerByIdentity("TP");
    }*/


    @Test
    void shouldCreateNewLoanApplicationWhenCustomerExistsAndIdentifierIsUnique() {
        // given
        Customer customer = Customer.builder().identity("TP").email("test@gmail.com").build();

        LoanApplicationRequest request = new LoanApplicationRequest(LoanType.AUTO,12,500000.0,"TP");
        // when

        when(restClient.findCustomerByIdentity(request.customerIdentity())).thenReturn(customer);
        when(repository.findByIdentifierIgnoreCase(Mockito.anyString())).thenReturn(Optional.empty());

        underTest.createNewLoanApplication(request);
        // then
        ArgumentCaptor<LoanApplication> captorLoan = ArgumentCaptor.forClass(LoanApplication.class);
        verify(repository).save(captorLoan.capture());
        LoanApplication savedLoanApplication = captorLoan.getValue();

        assertThat(savedLoanApplication).isNotNull();
        assertThat(savedLoanApplication.getLoanType()).isEqualTo(request.type());
        assertThat(savedLoanApplication.getLoanTerm()).isEqualTo(request.term());
        assertThat(savedLoanApplication.getRequestedAmount()).isEqualTo(request.amount());
        assertThat(savedLoanApplication.getCustomerIdentity()).isEqualTo(request.customerIdentity());
    }

    @Test
    void shouldNotCreateLoanApplication(){
        // given
        Customer customer = Customer.builder().identity("TP").email("test@gmail.com").build();

        LoanApplicationRequest request = new LoanApplicationRequest(LoanType.AUTO,12,500000.0,"TP");
        LoanApplication loanApplication = new LoanApplication();
        // when

        when(restClient.findCustomerByIdentity(request.customerIdentity())).thenReturn(customer);
        when(repository.findByIdentifierIgnoreCase(Mockito.anyString())).thenReturn(Optional.of(loanApplication));
        // then

        assertThatThrownBy(() -> underTest.createNewLoanApplication(request))
                .isInstanceOf(ResourceAlreadyExists.class)
                .hasMessage("Identifiant de la demande existant déjà, veuillez soumettre une autre demande ");
        verify(repository,Mockito.never()).save(Mockito.any(LoanApplication.class));
    }

    @Test
    @Disabled
    void approveLoanApplication() {
    }

    @Test
    @Disabled
    void declineLoanApplication() {
    }

    @Test
    @Disabled
    void findLoanApplication() {
    }

    @Test
    @Disabled
    void removeLoanApplication() {
    }
}