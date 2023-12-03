package lv.gstg.javademo.transactions.repositories;

import lv.gstg.javademo.transactions.DatabaseIntegrationTest;
import lv.gstg.javademo.transactions.model.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountsRepositoryIntegrationTest extends DatabaseIntegrationTest {

    @Autowired
    AccountsRepository accountsRepository;
    @Autowired
    TransactionsRepository transactionsRepository;

    @Test
    @Transactional
    void saveAccount() {
        Account entity = new Account();
        entity.setCurrency("USD");
        entity.setBalance(BigDecimal.ZERO);
        entity.setClientId(12345L);
        entity.setAccountNr("IE12BOFI90000112345678");
        entity = accountsRepository.saveAndFlush(entity);
        System.out.println(entity);
        assertThat(entity.getId()).isNotNull();

        entity = accountsRepository.findById(entity.getId()).orElseThrow();
        assertThat(entity).isNotNull();
    }

    @Test
    @Transactional
    void findAll() {
        var accounts = accountsRepository.findAll();
        assertThat(accounts).hasSize(4); // must match the number of accounts created with migration/B002__data.sql
    }

    @Test
    @Transactional
    void findAllByIdAndLockForUpdate() {
        accountsRepository.findAllByIdAndLockForUpdate(Set.of(1L, 2L));
    }
}
