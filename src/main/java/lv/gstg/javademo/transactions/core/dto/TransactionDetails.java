package lv.gstg.javademo.transactions.core.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
public class TransactionDetails {

    Long id;
    ZonedDateTime createdAt;
    Long sourceAccountNr;
    Long targetAccountNr;
    BigDecimal amount;
    String currency;
    BigDecimal sourceCurrencyRate;
    BigDecimal sourceAmount;
    BigDecimal sourceBalanceAfter;
    BigDecimal targetBalanceAfter;

}
