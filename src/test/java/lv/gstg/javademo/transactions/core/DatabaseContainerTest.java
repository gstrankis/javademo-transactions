package lv.gstg.javademo.transactions.core;

import lv.gstg.javademo.transactions.model.Account;
import lv.gstg.javademo.transactions.repositories.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public class DatabaseContainerTest {
    //See how-to resources regarding Testcontainers
    // https://spring.io/blog/2023/06/23/improved-testcontainers-support-in-spring-boot-3-1
    // https://testcontainers.com/guides/testing-spring-boot-rest-api-using-testcontainers/

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Autowired
    AccountRepository accountRepository;

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

    @Test
    @Transactional
    void saveNewAccount() {
        Account entity = Account.builder()
                .currency("USD")
                .balance(BigDecimal.ZERO)
                .clientId(12345L)
                .accountNr("IE12BOFI90000112345678")
                .build();
        entity = accountRepository.saveAndFlush(entity);
        System.out.println(entity.toString());
        assertThat(entity.getId()).isNotNull();
    }

}
