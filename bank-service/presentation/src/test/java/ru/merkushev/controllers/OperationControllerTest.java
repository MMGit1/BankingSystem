package ru.merkushev.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class OperationControllerTest extends AbstractControllerIntegrationTest {

    @Test
    void deposit_andWithdraw_shouldWork() throws Exception {
        UUID userId = createUser("user1", "User One", 20, "MEN", "brown");
        UUID accountId = createAccount(userId);

        deposit("user1", accountId, "1000.00");
        withdraw("user1", accountId, "250.00");

        BigDecimal balance = getBalance(accountId);
        assertEquals(0, balance.compareTo(new BigDecimal("750.00")));
    }

    @Test
    void transfer_andGetOperations_shouldWork() throws Exception {
        UUID user1Id = createUser("sender", "Sender", 20, "MEN", "brown");
        UUID user2Id = createUser("receiver", "Receiver", 21, "WOMEN", "black");

        UUID account1Id = createAccount(user1Id);
        UUID account2Id = createAccount(user2Id);

        addFriend("sender", user2Id);

        deposit("sender", account1Id, "1000.00");
        withdraw("sender", account1Id, "100.00");
        transfer(account1Id, account2Id);

        BigDecimal account1Balance = getBalance(account1Id);
        BigDecimal account2Balance = getBalance(account2Id);

        assertEquals(0, account1Balance.compareTo(new BigDecimal("797.00")));
        assertEquals(0, account2Balance.compareTo(new BigDecimal("100.00")));

        String operationsResponse = mockMvc.perform(get("/operations")
                        .with(client("sender"))
                        .param("accountId", account1Id.toString())
                        .param("type", "TRANSFER_OUT"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode operationsJson = objectMapper.readTree(operationsResponse);

        assertEquals(1, operationsJson.size());
        assertEquals("TRANSFER_OUT", operationsJson.get(0).get("type").asText());
        assertEquals(account1Id.toString(), operationsJson.get(0).get("accountId").asText());
        assertEquals(account2Id.toString(), operationsJson.get(0).get("relatedAccountId").asText());
        assertEquals(0, new BigDecimal(operationsJson.get(0).get("amount").asText()).compareTo(new BigDecimal("100.00")));
        assertEquals(0, new BigDecimal(operationsJson.get(0).get("commission").asText()).compareTo(new BigDecimal("3.00")));
    }

    @Test
    void transfer_shouldReturnBadRequest_whenNotEnoughMoney() throws Exception {
        UUID user1Id = createUser("sender", "Sender", 20, "MEN", "brown");
        UUID user2Id = createUser("receiver", "Receiver", 21, "WOMEN", "black");

        UUID account1Id = createAccount(user1Id);
        UUID account2Id = createAccount(user2Id);

        deposit("sender", account1Id, "50.00");

        String requestBody = """
                {
                  "fromAccountId": "%s",
                  "toAccountId": "%s",
                  "amount": 100.00
                }
                """.formatted(account1Id, account2Id);

        mockMvc.perform(post("/operations/transfers")
                        .with(client("sender"))
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void clientCannotDepositToForeignAccount_shouldReturnForbidden() throws Exception {
        UUID ownerId = createUser("owner", "Owner", 20, "MEN", "brown");
        createUser("attacker", "Attacker", 22, "MEN", "black");

        UUID ownerAccountId = createAccount(ownerId);

        String requestBody = """
                {
                  "accountId": "%s",
                  "amount": 100.00
                }
                """.formatted(ownerAccountId);

        mockMvc.perform(post("/operations/deposits")
                        .with(client("attacker"))
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }
}