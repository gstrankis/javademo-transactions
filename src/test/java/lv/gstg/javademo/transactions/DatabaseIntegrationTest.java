package lv.gstg.javademo.transactions;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
//See https://stackoverflow.com/questions/68660093/testcontainers-loss-of-connection-after-some-tests-running
public abstract class DatabaseIntegrationTest {
    //See how-to resources regarding Testcontainers
    // https://spring.io/blog/2023/06/23/improved-testcontainers-support-in-spring-boot-3-1
    // https://testcontainers.com/guides/testing-spring-boot-rest-api-using-testcontainers/

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.5")
            .withReuse(true);

    @BeforeAll
    public static void beforeAll() {
        postgres.start();
    }

    @DynamicPropertySource
    static void configureDynamicProperties(DynamicPropertyRegistry registry) {
        //In scope of Database testing we also check flyway migrations and validate schema
        registry.add("spring.flyway.enabled", () -> true);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    }

    @Test
    void testConnection() {
        assertTrue(postgres.isRunning());
    }

}
