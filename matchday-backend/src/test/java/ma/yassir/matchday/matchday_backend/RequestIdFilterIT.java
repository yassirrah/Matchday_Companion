package ma.yassir.matchday.matchday_backend;

import ma.yassir.matchday.matchday_backend.infra.logging.RequestIdFilter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@Testcontainers
public class RequestIdFilterIT {

    @Autowired
    private RestTestClient restTestClient;


    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("matchday")
            .withUsername("matchday")
            .withPassword("matchday");



    @Test
    void contextLoads() {
    }

    @Test
    void generates_requestId_when_missing(){
        restTestClient
                .get()
                .uri("/v3/api-docs")
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader().exists(RequestIdFilter.HEADER);
    }

    @Test
    void propagate_requestId_when_provided(){
        String rid = "test-req-123";
        restTestClient.get()
                .uri("/v3/api-docs")
                .header(RequestIdFilter.HEADER, rid)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .valueEquals(RequestIdFilter.HEADER, rid);
    }
}
