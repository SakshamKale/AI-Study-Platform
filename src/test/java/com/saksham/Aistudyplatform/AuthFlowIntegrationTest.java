package com.saksham.Aistudyplatform;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class AuthFlowIntegrationTest {

    @Autowired
    private WebApplicationContext applicationContext;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @BeforeEach
    void setUpMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .addFilters(springSecurityFilterChain)
                .build();
    }

    @Test
    void usersCanOnlyAccessTheirOwnTopicsAndRegistrationDoesNotReturnPassword()
            throws Exception {
        String userAEmail = "user-a-" + UUID.randomUUID() + "@example.com";
        String userBEmail = "user-b-" + UUID.randomUUID() + "@example.com";
        String password = "test-password-123";

        JsonNode registrationResponse = register(userAEmail, password);
        assertEquals(userAEmail, registrationResponse.path("email").asText());
        assertFalse(registrationResponse.has("password"));

        String userAToken = login(userAEmail, password);
        long topicId = createTopic(userAToken);

        register(userBEmail, password);
        String userBToken = login(userBEmail, password);

        JsonNode userBTopics = getTopics(userBToken);
        assertTrue(userBTopics.isArray());
        assertTrue(userBTopics.isEmpty());

        mockMvc.perform(get("/api/topics/{id}", topicId)
                        .header("Authorization", "Bearer " + userBToken))
                .andExpect(status().isNotFound());

        mockMvc.perform(put("/api/topics/{id}", topicId)
                        .header("Authorization", "Bearer " + userBToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"subject\":\"Java\",\"topic\":\"Changed\"}"))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/topics/{id}", topicId)
                        .header("Authorization", "Bearer " + userBToken))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/topics/{id}", topicId)
                        .header("Authorization", "Bearer " + userAToken))
                .andExpect(status().isOk());

        TestTransaction.flagForRollback();
    }

    private JsonNode register(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegistrationRequest(
                                "Integration Test", email, password))))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    private String login(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(
                                email, password))))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString())
                .path("token").asText();
    }

    private long createTopic(String token) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/topics")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"subject\":\"Java\",\"topic\":\"OOP\"}"))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString())
                .path("id").asLong();
    }

    private JsonNode getTopics(String token) throws Exception {
        MvcResult result = mockMvc.perform(get("/api/topics")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    private record RegistrationRequest(String name, String email, String password) {}

    private record LoginRequest(String email, String password) {}
}
