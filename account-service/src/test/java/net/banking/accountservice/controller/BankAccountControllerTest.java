package net.banking.accountservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.banking.accountservice.dto.bankaccount.ChangeAccountStatus;
import net.banking.accountservice.dto.currentaccount.CurrentAccountRequest;
import net.banking.accountservice.enums.AccountStatus;
import net.banking.accountservice.service.BankAccountService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@WebMvcTest(BankAccountController.class)
class BankAccountControllerTest {
    @MockBean
    private BankAccountService service;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;

    @Test
    void shouldSaveNewCurrentAccount() throws Exception {
        CurrentAccountRequest request = new CurrentAccountRequest(500.0,"MAD","RABAT","RG45");

        Mockito.doNothing().when(service).createNewCurrentAccount(Mockito.any());
        mvc.perform(MockMvcRequestBuilders.post("/api/accounts/newCurrentAccount")
                     .contentType(MediaType.APPLICATION_JSON_VALUE)
                     .content(mapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("Compte a été créé avec succès pour le client identifié par RG45"));
    }

    @Test
    void shouldChangeAccountStatusTo() throws Exception {
        String rib = "test";
        ChangeAccountStatus request = new ChangeAccountStatus(AccountStatus.CLOSED);

        Mockito.doNothing().when(service).changeAccountStatus(Mockito.eq(rib), Mockito.any());
        mvc.perform(MockMvcRequestBuilders.post("/api/accounts/{rib}/changeStatus",rib)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(mapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isAccepted())
                .andExpect(MockMvcResultMatchers.content().string("Compte identifié par le rib test a été modifié avec succès"));
    }
}