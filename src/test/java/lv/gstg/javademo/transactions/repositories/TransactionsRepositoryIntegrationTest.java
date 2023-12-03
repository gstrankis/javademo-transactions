package lv.gstg.javademo.transactions.repositories;

import lv.gstg.javademo.transactions.DatabaseIntegrationTest;
import lv.gstg.javademo.transactions.model.Account;
import lv.gstg.javademo.transactions.model.Transaction;
import lv.gstg.javademo.transactions.model.Transfer;
import lv.gstg.javademo.transactions.model.TxType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Limit;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.random.RandomGenerator;

import static org.assertj.core.api.Assertions.assertThat;

public class TransactionsRepositoryIntegrationTest extends DatabaseIntegrationTest {

    @Autowired
    AccountsRepository accountsRepository;
    @Autowired
    TransfersRepository transfersRepository;
    @Autowired
    TransactionsRepository transactionsRepository;

    @Test
    @Transactional
    void saveTransaction() {
        var account1 = accountsRepository.findById(1L).orElseThrow();
        var account2 = accountsRepository.findById(2L).orElseThrow();
        var tr = createTransfer(account1, account2);
        var entity = createDebitTransaction(tr);

        transactionsRepository.saveAndFlush(entity);
        System.out.println(entity);
        assertThat(entity.getId()).isNotNull();

        entity = transactionsRepository.findById(entity.getId()).orElseThrow();
        assertThat(entity).isNotNull();
    }


    @Test
    @Transactional
    void findAllByAccountId() {
        var account1 = accountsRepository.findById(1L).orElseThrow();
        var account2 = accountsRepository.findById(2L).orElseThrow();
        var account3 = accountsRepository.findById(3L).orElseThrow();
        generateTransactions(account1, account2, 8);
        generateTransactions(account2, account3, 1);
        transactionsRepository.flush();

//        var list = transactionsRepository.findAllByAccountId(account1.getId(), ScrollPosition.offset(0), Limit.unlimited());
        System.out.println("findAllByAccountId, no limit, no offset");
        var list = transactionsRepository.findAllByAccountId(account1.getId());
        list.forEach(t -> System.out.println(t.getId() + " " + t.getCreatedAt()));
        assertThat(list).hasSize(8)
                .isSortedAccordingTo(Comparator.comparing(Transaction::getCreatedAt).reversed());

        System.out.println("findAllByAccountIdLimited, Limit.of(5), no offset");
        list = transactionsRepository.findAllByAccountIdLimited(account1.getId(), Limit.of(5));
        assertThat(list).hasSize(5)
                .isSortedAccordingTo(Comparator.comparing(Transaction::getCreatedAt).reversed());

        System.out.println("findAllByAccountIdPageable, offset=0, limit=5");
        list = transactionsRepository.findAllByAccountIdPageable(account1.getId(), OffsetBasedPageRequest.of(0, 5));
        assertThat(list).hasSize(5)
                .isSortedAccordingTo(Comparator.comparing(Transaction::getCreatedAt).reversed());

        System.out.println("findAllByAccountIdPageable, offset=5, limit=5");
        list = transactionsRepository.findAllByAccountIdPageable(account1.getId(), OffsetBasedPageRequest.of(5, 5));
        assertThat(list).hasSize(3)
                .isSortedAccordingTo(Comparator.comparing(Transaction::getCreatedAt).reversed());

        System.out.println("findAllByAccountIdUsingNativeQuery offset=0, limit=MAX_VALUE");
        list = transactionsRepository.findAllByAccountIdUsingNativeQuery(account1.getId(), 0, Integer.MAX_VALUE);
        assertThat(list).hasSize(8)
                .isSortedAccordingTo(Comparator.comparing(Transaction::getCreatedAt).reversed());

        System.out.println("findAllByAccountIdUsingNativeQuery offset=0, limit=5");
        list = transactionsRepository.findAllByAccountIdUsingNativeQuery(account1.getId(), 0, 5);
        assertThat(list).hasSize(5)
                .isSortedAccordingTo(Comparator.comparing(Transaction::getCreatedAt).reversed());

        System.out.println("findAllByAccountIdUsingNativeQuery offset=5, limit=5");
        list = transactionsRepository.findAllByAccountIdUsingNativeQuery(account1.getId(), 5, 5);
        assertThat(list).hasSize(3)
                .isSortedAccordingTo(Comparator.comparing(Transaction::getCreatedAt).reversed());
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
        return transfersRepository.save(entity);
    }

    private Transaction createDebitTransaction(Transfer tr) {
        var account = tr.getSourceAccount();
        var entity = Transaction.builder()
                .createdAt(tr.getCreatedAt())
                .account(tr.getSourceAccount())
                .txType(TxType.D)
                .amount(BigDecimal.TEN)
                .balanceAfter(BigDecimal.TEN)
                .transfer(tr)
                .build();
        return entity;
    }

    private void generateTransactions(Account account1, Account account2, int number) {
        for (int i = 0; i < number; i++) {
            var tr = createTransfer(account1, account2);
            tr.setCreatedAt(LocalDate.now().atTime(randomTime()));
            var entity = createDebitTransaction(tr);
            transactionsRepository.save(entity);
        }
    }

    private LocalTime randomTime() {
        return LocalTime.ofSecondOfDay(RandomGenerator.getDefault().nextLong(60 * 60 * 24));
    }

}
