package lv.gstg.javademo.transactions.core;

import lv.gstg.javademo.transactions.core.dto.TransferFundsRequest;
import lv.gstg.javademo.transactions.exceptions.BadRequestException;
import lv.gstg.javademo.transactions.external.currencyrates.ExchangeRateProvider;
import lv.gstg.javademo.transactions.model.Account;
import lv.gstg.javademo.transactions.model.TxType;
import lv.gstg.javademo.transactions.repositories.AccountsRepository;
import lv.gstg.javademo.transactions.repositories.TransactionsRepository;
import lv.gstg.javademo.transactions.repositories.TransfersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@Import({AccountsService.class, AccountMapperImpl.class, TransactionMapperImpl.class, CurrencyConverter.class})
public class AccountsServiceTest {
    @MockBean
    ExchangeRateProvider exchangeRateProvider;
    @MockBean
    AccountsRepository accountsRepository;
    @MockBean
    TransfersRepository transfersRepository;
    @MockBean
    TransactionsRepository transactionsRepository;
    @Autowired
    CurrencyConverter currencyConverter;
    @Autowired
    AccountsService service;

    @BeforeEach
    void init() {
        currencyConverter.setRates(Map.of("USDEUR", new BigDecimal("0.90")));
    }

    @Test
    void transferFunds() {
        var a1 = mockUsdAccount(1L, 100);
        var a2 = mockUsdAccount(2L, 100);
        when(accountsRepository.findAllByIdAndLockForUpdate(any()))
                .thenReturn(List.of(a1, a2));

        var request = TransferFundsRequest.builder()
                .sourceAccountId(1L)
                .targetAccountId(2L)
                .amount(new BigDecimal("100.00"))
                .currency("USD")
                .build();
        service.transferFunds(request);
    }

    @Test
    void transferFunds_expectNotEnough() {
        var a1 = mockUsdAccount(1L, 100);
        var a2 = mockUsdAccount(2L, 100);
        when(accountsRepository.findAllByIdAndLockForUpdate(any()))
                .thenReturn(List.of(a1, a2));

        var request = TransferFundsRequest.builder()
                .sourceAccountId(1L)
                .targetAccountId(2L)
                .amount(new BigDecimal("101.00"))
                .currency("USD")
                .build();

        assertThatThrownBy(() -> service.transferFunds(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Not enough funds source.balance: 100 to withdraw 101.00");
    }

    @Test
    void transferFunds_expectWrongCurrency() {
        var a1 = mockUsdAccount(1L, 100);
        var a2 = mockUsdAccount(2L, 100);
        when(accountsRepository.findAllByIdAndLockForUpdate(any()))
                .thenReturn(List.of(a1, a2));

        var request = TransferFundsRequest.builder()
                .sourceAccountId(1L)
                .targetAccountId(2L)
                .amount(new BigDecimal("100.00"))
                .currency("EUR") //target account is USD
                .build();

        assertThatThrownBy(() -> service.transferFunds(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Target account currency USD doesn't match transfer currency EUR");
    }

    @Test
    void transferFunds_expectCurrencyConversion() {
        var a1 = mockEurAccount(1L, 100);
        var a2 = mockUsdAccount(2L, 100);
        when(accountsRepository.findAllByIdAndLockForUpdate(any()))
                .thenReturn(List.of(a1, a2));

        var request = TransferFundsRequest.builder()
                .sourceAccountId(1L)
                .targetAccountId(2L)
                .amount(new BigDecimal("100.00"))
                .currency("USD") //source account is EUR
                .build();

        service.transferFunds(request);

        verify(transactionsRepository).saveAndFlush(ArgumentMatchers.argThat(t -> t.getAccount().getId().equals(a1.getId())
                && t.getTxType() == TxType.D
                && t.getAmount().equals(new BigDecimal("90.00"))
        ));
        verify(transactionsRepository).saveAndFlush(ArgumentMatchers.argThat(t -> t.getAccount().getId().equals(a2.getId())
                && t.getTxType() == TxType.C
                && t.getAmount().equals(new BigDecimal("100.00"))
        ));

    }

    private Account mockUsdAccount(Long id, double balance) {
        var a = new Account();
        a.setId(id);
        a.setCurrency("USD");
        a.setBalance(new BigDecimal(balance));
        return a;
    }

    private Account mockEurAccount(Long id, double balance) {
        var a = mockUsdAccount(id, balance);
        a.setCurrency("EUR");
        return a;
    }
}
