package lv.gstg.javademo.transactions.repositories;

import lv.gstg.javademo.transactions.DatabaseIntegrationTest;
import lv.gstg.javademo.transactions.model.Account;
import lv.gstg.javademo.transactions.model.Transfer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class TransfersRepositoryIntegrationTest extends DatabaseIntegrationTest {

    @Autowired
    AccountsRepository accountsRepository;
    @Autowired
    TransfersRepository transfersRepository;

    @Test
    @Transactional
    void saveTransfer() {
        var account1 = accountsRepository.findById(1L).orElseThrow();
        var account2 = accountsRepository.findById(2L).orElseThrow();
        var entity = createTransfer(account1, account2);

        transfersRepository.saveAndFlush(entity);
        System.out.println(entity);
        assertThat(entity.getId()).isNotNull();

        entity = transfersRepository.findById(entity.getId()).orElseThrow();
        assertThat(entity).isNotNull();
    }

    private Transfer createTransfer(Account account1, Account account2) {
        var entity = new Transfer();
        entity.setCreatedAt(LocalDateTime.now());
        entity.setSourceAccount(account1);
        entity.setTargetAccount(account2);
        entity.setAmount(BigDecimal.TEN);
        entity.setCurrency("USD");
        entity.setSourceCurrencyRate(BigDecimal.ONE);
        entity.setTargetCurrencyRate(BigDecimal.ONE);
        return entity;
    }


}
