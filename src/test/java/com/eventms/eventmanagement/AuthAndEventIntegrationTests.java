package com.eventms.eventmanagement;

import com.eventms.eventmanagement.dto.AuthResponse;
import com.eventms.eventmanagement.model.Event;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthAndEventIntegrationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String token;
    private Long createdEventId;

    private String baseUrl() {
        return "http://localhost:" + port + "/api";
    }

    @Test
    @Order(1)
    void registerReturnsToken() {
        var payload = """
                {"name":"Test User","email":"test@example.com","password":"password123"}
                """;
        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(baseUrl() + "/auth/register", payload, AuthResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).getToken()).isNotBlank();
        token = response.getBody().getToken();
    }

    @Test
    @Order(2)
    void loginReturnsToken() {
        var payload = """
                {"email":"test@example.com","password":"password123"}
                """;
        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(baseUrl() + "/auth/login", payload, AuthResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).getToken()).isNotBlank();
        token = response.getBody().getToken();
    }

    @Test
    @Order(3)
    void createEventWithAuth() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        var payload = """
                {"title":"Integration Event","description":"From test","location":"Test City"}
                """;
        HttpEntity<String> req = new HttpEntity<>(payload, headers);
        ResponseEntity<Event> response = restTemplate.exchange(baseUrl() + "/events", HttpMethod.POST, req, Event.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Event body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getId()).isNotNull();
        createdEventId = body.getId();
    }

    @Test
    @Order(4)
    void listEventsContainsCreated() {
        ResponseEntity<Event[]> response = restTemplate.getForEntity(baseUrl() + "/events", Event[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Event> events = Arrays.asList(Objects.requireNonNull(response.getBody()));
        assertThat(events.stream().anyMatch(e -> Objects.equals(e.getId(), createdEventId))).isTrue();
    }

    @Test
    @Order(5)
    void getEventById() {
        ResponseEntity<Event> response = restTemplate.getForEntity(baseUrl() + "/events/" + createdEventId, Event.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).getId()).isEqualTo(createdEventId);
    }
}
