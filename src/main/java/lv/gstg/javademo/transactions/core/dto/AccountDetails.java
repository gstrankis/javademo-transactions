package lv.gstg.javademo.transactions.core.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountDetails {

    Long id;
    Long clientId;
    String accountNr;
    String currency;
    BigDecimal balance;

}
