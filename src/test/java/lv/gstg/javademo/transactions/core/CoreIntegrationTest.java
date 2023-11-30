package lv.gstg.javademo.transactions.core;

import lv.gstg.javademo.transactions.model.Account;
import lv.gstg.javademo.transactions.repositories.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
@ActiveProfiles("it")
public class CoreIntegrationTest {
    //See Testcontainers resources
    // https://spring.io/blog/2023/06/23/improved-testcontainers-support-in-spring-boot-3-1
    // https://testcontainers.com/guides/testing-spring-boot-rest-api-using-testcontainers/

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Autowired
    AccountRepository accountRepository;

    @Test
    void testConnection() {
        //along the way this tests flyway migrations and validates schema
        assertTrue(postgres.isRunning());
    }

    @Test
    @Transactional
    void saveNewAccount() {
        Account entity = new Account();
        entity.setCurrency("USD");
        entity.setBalance(BigDecimal.ZERO);
        entity.setClientId(12345L);
        entity = accountRepository.save(entity);
        System.out.println(entity.toString());
        assertThat(entity.getId()).isNotNull();
    }
}
