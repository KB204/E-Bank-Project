package net.banking.loanservice.controller;

import net.banking.loanservice.dto.loan_application.LoanApplicationRequest;
import net.banking.loanservice.dto.loan_application.LoanApplicationResponse;
import net.banking.loanservice.enums.LoanType;
import net.banking.loanservice.service.LoanApplicationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@WebMvcTest(LoanApplicationController.class)
@ActiveProfiles("test")
class LoanApplicationControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private LoanApplicationService service;

    @Test
    void shouldSaveNewLoanApplication() throws Exception{
        LoanApplicationRequest request = new LoanApplicationRequest(LoanType.OTHER,40,350000d,"Test");

        Mockito.doNothing().when(service).createNewLoanApplication(Mockito.any());
        mvc.perform(MockMvcRequestBuilders.post("/api/loanApplications")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("Votre demande de crédit a été transmise avec succès. Vous serez informé par email pour obtenir les détails de votre demande"));
    }

    @Test
    void shouldFindLoanApplication() throws Exception {
        LoanApplicationResponse response = LoanApplicationResponse.builder().identifier("Test").loanType(LoanType.STUDENT).build();

        Mockito.when(service.findLoanApplication(response.identifier())).thenReturn(response);
        mvc.perform(MockMvcRequestBuilders.get("/api/loanApplications/searchFor/{identity}",response.identifier()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(response)));
    }
}