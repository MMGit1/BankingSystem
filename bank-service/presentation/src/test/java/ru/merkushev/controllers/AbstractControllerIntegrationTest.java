package ru.merkushev.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.merkushev.BankApplication;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(
        classes = BankApplication.class,
        properties = {
                "springdoc.api-docs.enabled=false",
                "springdoc.swagger-ui.enabled=false",
                "spring.jpa.hibernate.ddl-auto=create-drop"
        }
)
@AutoConfigureMockMvc
public abstract class AbstractControllerIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
    }

    @Autowired
    protected MockMvc mockMvc;

    protected final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.execute("""
            TRUNCATE TABLE user_friends, operations, bank_accounts, users CASCADE
            """);

        jdbcTemplate.update("""
            INSERT INTO users (id, age, gender, hair_color, login, name, password_hash, role)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """,
                UUID.fromString("00000000-0000-0000-0000-000000000001"),
                0,
                "MEN",
                "__admin__",
                "admin",
                "Administrator",
                new BCryptPasswordEncoder().encode("admin"),
                "ADMIN"
        );
    }

    protected RequestPostProcessor admin() {
        return user("admin").roles("ADMIN");
    }

    protected RequestPostProcessor client(String login) {
        return user(login).roles("CLIENT");
    }

    protected UUID createUser(String login, String name, int age, String gender, String hairColor) throws Exception {
        String requestBody = """
                {
                  "login": "%s",
                  "password": "12345",
                  "name": "%s",
                  "age": %d,
                  "gender": "%s",
                  "hairColor": "%s",
                  "role": "CLIENT"
                }
                """.formatted(login, name, age, gender, hairColor);

        String response = mockMvc.perform(post("/users")
                        .with(admin())
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return UUID.fromString(objectMapper.readTree(response).get("id").asText());
    }

    protected void addFriend(String login, UUID friendId) throws Exception {
        mockMvc.perform(post("/users/me/friends/{friendId}", friendId)
                        .with(client(login)))
                .andExpect(status().isOk());
    }

    protected void removeFriend(UUID friendId) throws Exception {
        mockMvc.perform(delete("/users/me/friends/{friendId}", friendId)
                        .with(client("main_user")))
                .andExpect(status().isNoContent());
    }

    protected UUID createAccount(UUID userId) throws Exception {
        String requestBody = """
                {
                  "userId": "%s"
                }
                """.formatted(userId);

        String response = mockMvc.perform(post("/accounts")
                        .with(admin())
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return UUID.fromString(objectMapper.readTree(response).get("id").asText());
    }

    protected void deposit(String login, UUID accountId, String amount) throws Exception {
        String requestBody = """
                {
                  "accountId": "%s",
                  "amount": %s
                }
                """.formatted(accountId, amount);

        mockMvc.perform(post("/operations/deposits")
                        .with(client(login))
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    protected void withdraw(String login, UUID accountId, String amount) throws Exception {
        String requestBody = """
                {
                  "accountId": "%s",
                  "amount": %s
                }
                """.formatted(accountId, amount);

        mockMvc.perform(post("/operations/withdrawals")
                        .with(client(login))
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    protected void transfer(UUID fromAccountId, UUID toAccountId) throws Exception {
        String requestBody = """
                {
                  "fromAccountId": "%s",
                  "toAccountId": "%s",
                  "amount": %s
                }
                """.formatted(fromAccountId, toAccountId, "100.00");

        mockMvc.perform(post("/operations/transfers")
                        .with(client("sender"))
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    protected BigDecimal getBalance(UUID accountId) throws Exception {
        String response = mockMvc.perform(get("/accounts/{accountId}/balance", accountId)
                        .with(admin()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(response);
        return new BigDecimal(json.get("balance").asText());
    }
}