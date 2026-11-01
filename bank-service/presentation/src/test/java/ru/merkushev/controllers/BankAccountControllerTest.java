package ru.merkushev.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class BankAccountControllerTest extends AbstractControllerIntegrationTest {

    @Test
    void adminCreateAccount_andGetBankAccountById_shouldWork() throws Exception {
        UUID userId = createUser("u1", "User One", 20, "MEN", "brown");
        UUID accountId = createAccount(userId);

        String response = mockMvc.perform(get("/accounts/{accountId}", accountId)
                        .with(admin()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(response);

        assertEquals(accountId.toString(), json.get("id").asText());
        assertEquals(userId.toString(), json.get("ownerId").asText());
        assertEquals(0, new BigDecimal(json.get("balance").asText()).compareTo(BigDecimal.ZERO));
    }

    @Test
    void clientGetAccountsMe_shouldReturnOnlyOwnAccounts() throws Exception {
        UUID user1Id = createUser("u1", "User One", 20, "MEN", "brown");
        UUID user2Id = createUser("u2", "User Two", 21, "WOMEN", "black");

        createAccount(user1Id);
        createAccount(user1Id);
        createAccount(user2Id);

        String response = mockMvc.perform(get("/accounts/me")
                        .with(client("u1")))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(response);
        assertEquals(2, json.size());
    }

    @Test
    void clientCannotGetForeignAccount_shouldReturnForbidden() throws Exception {
        UUID user1Id = createUser("u1", "User One", 20, "MEN", "brown");
        UUID user2Id = createUser("u2", "User Two", 21, "WOMEN", "black");

        createAccount(user1Id);
        UUID foreignAccountId = createAccount(user2Id);

        mockMvc.perform(get("/accounts/{accountId}", foreignAccountId)
                        .with(client("u1")))
                .andExpect(status().isForbidden());
    }

    @Test
    void getBalance_shouldReturnActualBalance() throws Exception {
        UUID userId = createUser("u1", "User One", 20, "MEN", "brown");
        UUID accountId = createAccount(userId);

        deposit("u1", accountId, "500.00");

        BigDecimal balance = getBalance(accountId);
        assertEquals(0, balance.compareTo(new BigDecimal("500.00")));
    }
}