package lv.gstg.javademo.transactions.core.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TransferFundsRequest {

    Long sourceAccountId;
    Long targetAccountId;
    BigDecimal amount;
    String currency;

}
