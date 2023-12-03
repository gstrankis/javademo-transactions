package lv.gstg.javademo.transactions.core;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import lv.gstg.javademo.transactions.DatabaseIntegrationTest;
import lv.gstg.javademo.transactions.core.dto.TransferFundsRequest;
import lv.gstg.javademo.transactions.repositories.AccountsRepository;
import lv.gstg.javademo.transactions.repositories.TransactionsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class AccountsServiceIntegrationTest extends DatabaseIntegrationTest {

    @Autowired
    AccountsService accountsService;
    @Autowired
    AccountsRepository accountsRepository;
    @Autowired
    TransactionsRepository transactionsRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    @Transactional
    void findAccountHistory() {
        Long accountId = 1L;
        accountsService.findAccountHistory(accountId, 0, 100);
    }

    @Test
    @Transactional
    void transferFunds() {
        var request = TransferFundsRequest.builder()
                .sourceAccountId(1L)
                .targetAccountId(3L)
                .amount(new BigDecimal("100.00"))
                .currency("USD")
                .build();
        accountsService.transferFunds(request);

        var a1 = accountsRepository.findById(1L).orElseThrow();
        var a2 = accountsRepository.findById(3L).orElseThrow();
        assertThat(a1.getBalance()).isEqualByComparingTo("1900.00");
        assertThat(a2.getBalance()).isEqualByComparingTo("2100.00");
    }

    @Test
    @Transactional
    void transferFundsConcurrently() throws InterruptedException {
        var pool = Executors.newFixedThreadPool(9);
        final AtomicInteger successes = new AtomicInteger(0);
        final AtomicInteger failures = new AtomicInteger(0);

        //prepare transfer requests for execution
        //some amount from a->b, some from b->a
        var request1 = TransferFundsRequest.builder()
                .sourceAccountId(1L)
                .targetAccountId(3L)
                .amount(new BigDecimal("2.00"))
                .currency("USD")
                .build();
        var request2 = TransferFundsRequest.builder()
                .sourceAccountId(3L)
                .targetAccountId(1L)
                .amount(new BigDecimal("1.00"))
                .currency("USD")
                .build();

        //start execution
        for (int i = 0; i < 20; i++) {
            pool.submit(() -> transferFunds(request1, successes, failures));
            pool.submit(() -> transferFunds(request2, successes, failures));
        }

        //await for execution to complete
        pool.shutdown();
        boolean terminatedInTime;
        if (!(terminatedInTime = pool.awaitTermination(15, TimeUnit.SECONDS)))
            log.warn("Pool still running!");

        //check results
        System.out.println("Successes: " + successes.intValue() + " Failures: " + failures.intValue());
        assertThat(terminatedInTime).withFailMessage("Pool should terminate in given timeout")
                .isTrue();
        assertThat(failures.intValue()).withFailMessage("There should be no failures")
                .isEqualTo(0);

        System.out.println("***** retrieving A1,A2 *****");
        var a1 = accountsRepository.findById(1L).orElseThrow();
        var a2 = accountsRepository.findById(3L).orElseThrow();

        System.out.println("***** transactionsRepository.findAllByAccountId A1 *****");
        transactionsRepository.findAllByAccountId(a1.getId())
                .forEach(t -> System.out.println("A1: " + t.getTxType() + " " + t.getAmount() + " " + t.getBalanceAfter()));
        System.out.println("***** transactionsRepository.findAllByAccountId A2 *****");
        transactionsRepository.findAllByAccountId(a2.getId())
                .forEach(t -> System.out.println("A2: " + t.getTxType() + " " + t.getAmount() + " " + t.getBalanceAfter()));

        assertThat(a1.getBalance()).isEqualByComparingTo("1980.00");
        assertThat(a2.getBalance()).isEqualByComparingTo("2020.00");
    }

    private void transferFunds(TransferFundsRequest request1, AtomicInteger successes, AtomicInteger failures) {
        try {
            accountsService.transferFunds(request1);
            successes.incrementAndGet();
        } catch (Exception e) {
            failures.incrementAndGet();
            log.error(e.getMessage(), e);
            //System.err.println(e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

}
