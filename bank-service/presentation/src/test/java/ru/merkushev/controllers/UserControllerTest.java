package ru.merkushev.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class UserControllerTest extends AbstractControllerIntegrationTest {

    @Test
    void adminCreateUser_andGetUserById_shouldWork() throws Exception {
        UUID userId = createUser("misha_login", "Mikhail", 20, "MEN", "brown");

        String response = mockMvc.perform(get("/users/{userId}", userId)
                        .with(admin()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(response);

        assertEquals(userId.toString(), json.get("id").asText());
        assertEquals("misha_login", json.get("login").asText());
        assertEquals("Mikhail", json.get("name").asText());
        assertEquals(20, json.get("age").asInt());
        assertEquals("MEN", json.get("gender").asText());
        assertEquals("brown", json.get("hairColor").asText());
    }

    @Test
    void adminGetUsers_withFilter_shouldReturnFilteredUsers() throws Exception {
        createUser("user1", "Ivan", 19, "MEN", "brown");
        createUser("user2", "Anna", 21, "WOMEN", "black");
        createUser("user3", "Petr", 25, "MEN", "black");

        String response = mockMvc.perform(get("/users")
                        .with(admin())
                        .param("gender", "MEN")
                        .param("hairColor", "black"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(response);

        assertEquals(1, json.size());
        assertEquals("user3", json.get(0).get("login").asText());
    }

    @Test
    void clientGetCurrentUser_shouldWork() throws Exception {
        UUID userId = createUser("client1", "Client One", 20, "MEN", "brown");

        String response = mockMvc.perform(get("/users/me")
                        .with(client("client1")))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(response);

        assertEquals(userId.toString(), json.get("id").asText());
        assertEquals("client1", json.get("login").asText());
        assertEquals("Client One", json.get("name").asText());
    }

    @Test
    void addFriend_getFriends_removeFriend_shouldWork() throws Exception {
        createUser("main_user", "Main User", 20, "MEN", "brown");
        UUID friend1Id = createUser("friend1", "Friend One", 21, "WOMEN", "black");
        UUID friend2Id = createUser("friend2", "Friend Two", 22, "MEN", "blonde");

        addFriend("main_user", friend1Id);
        addFriend("main_user", friend2Id);

        String friendsResponse = mockMvc.perform(get("/users/me/friends")
                        .with(client("main_user")))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode friendsJson = objectMapper.readTree(friendsResponse);
        assertEquals(2, friendsJson.size());

        removeFriend(friend1Id);

        String afterDeleteResponse = mockMvc.perform(get("/users/me/friends")
                        .with(client("main_user")))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode afterDeleteJson = objectMapper.readTree(afterDeleteResponse);
        assertEquals(1, afterDeleteJson.size());
        assertEquals(friend2Id.toString(), afterDeleteJson.get(0).get("id").asText());
    }
}