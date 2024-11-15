package net.banking.loanservice.dao;

import net.banking.loanservice.entities.LoanApplication;
import net.banking.loanservice.enums.ApplicationStatus;
import net.banking.loanservice.enums.LoanType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LoanApplicationRepositoryTest {
    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16");
    @Autowired
    LoanApplicationRepository repository;
    List<LoanApplication> loanApplications = new ArrayList<>();

    @BeforeEach
    void setUp() {
        System.out.println("--------------------------------------------");
        loanApplications = List.of(
                LoanApplication.builder().identifier("test").loanType(LoanType.AUTO).loanTerm(5).createdAt(LocalDate.now())
                        .requestedAmount(5000.0).status(ApplicationStatus.PENDING).build(),
                LoanApplication.builder().identifier("test1").loanType(LoanType.BUSINESS).loanTerm(25).createdAt(LocalDate.now())
                        .requestedAmount(95000.0).status(ApplicationStatus.PENDING).build()
        );
        repository.saveAll(loanApplications);
        System.out.println("----------------------------------------------");
    }
    @Test
    public void connectionEstablishedTest(){
        assertThat(postgreSQLContainer.isCreated()).isTrue();
        assertThat(postgreSQLContainer.isRunning()).isTrue();
    }

    @Test
    void shouldFindLoanApplicationByIdentifierAndIgnoreTheCase() {
        String identifier = "TEST";
        Optional<LoanApplication> loanApplication = repository.findByIdentifierIgnoreCase(identifier);
        assertThat(loanApplication).isPresent();
    }
    @Test
    void shouldNotFindLoanApplicationByIdentifier() {
        String identifier = "xxx";
        Optional<LoanApplication> loanApplication = repository.findByIdentifierIgnoreCase(identifier);
        assertThat(loanApplication).isEmpty();
    }
}