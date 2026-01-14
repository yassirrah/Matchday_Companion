package ma.yassir.matchday.matchday_backend;


import ma.yassir.matchday.matchday_backend.domain.CrowdStatus;
import ma.yassir.matchday.matchday_backend.repository.VenueStatusRepository;
import ma.yassir.matchday.matchday_backend.repository.VenueStatusUpdateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.assertj.core.api.Assertions.assertThat;


import java.util.Map;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class VenueStatusIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("matchday")
            .withUsername("matchday")
            .withPassword("matchday");


    @Test
    void contextLoads() {
    }

    @Autowired
    private RestTestClient rest;

    @Autowired
    private VenueStatusRepository venueStatusRepository;

    @Autowired
    private VenueStatusUpdateRepository venueStatusUpdateRepository;

    @BeforeEach
    void cleanDb() {
        venueStatusUpdateRepository.deleteAll();
        venueStatusRepository.deleteAll();
    }

    @Test
    void cooldownIsEnforced() {
        String venueId = "venue-001";
        String deviceId = "device-123";

        // First update → OK
        rest.post()
                .uri("/api/v1/venue-status/{venueId}", venueId)
                .header("X-Device-Id", deviceId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("status", "OK"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("OK");

        // Second update immediately → 429
        rest.post()
                .uri("/api/v1/venue-status/{venueId}", venueId)
                .header("X-Device-Id", deviceId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("status", "PACKED"))
                .exchange()
                .expectStatus().isEqualTo(429)
                .expectHeader().exists("Retry-After")
                .expectBody()
                .jsonPath("$.error").isEqualTo("COOLDOWN_ACTIVE");

        // DB assertions
        assertThat(venueStatusUpdateRepository.count()).isEqualTo(1);
        assertThat(venueStatusRepository.findById(venueId))
                .isPresent()
                .get()
                .extracting("status")
                .isEqualTo(CrowdStatus.OK);
    }

    @Test
    void differentDeviceIsAllowed() {
        String venueId = "venue-002";

        rest.post()
                .uri("/api/v1/venue-status/{venueId}", venueId)
                .header("X-Device-Id", "device-A")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("status", "OK"))
                .exchange()
                .expectStatus().isOk();

        rest.post()
                .uri("/api/v1/venue-status/{venueId}", venueId)
                .header("X-Device-Id", "device-B")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("status", "PACKED"))
                .exchange()
                .expectStatus().isOk();

        assertThat(venueStatusRepository.findById(venueId))
                .isPresent()
                .get()
                .extracting("status")
                .isEqualTo(CrowdStatus.PACKED);
    }
}