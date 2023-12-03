package lv.gstg.javademo.transactions.core.dto;

import lombok.Data;
import lv.gstg.javademo.transactions.model.TxType;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
public class TransactionDetails {
    Long id;
    ZonedDateTime createdAt;
    Long accountId;
    TxType txType;
    BigDecimal amount;
    BigDecimal balanceAfter;
    Long transferId;
}
