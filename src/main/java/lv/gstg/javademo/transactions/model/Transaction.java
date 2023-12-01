package lv.gstg.javademo.transactions.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@ToString
@Builder
public class Transaction extends BaseEntity {

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "source_account_id", nullable = false)
    Account sourceAccount;

    @ManyToOne
    @JoinColumn(name = "target_account_id", nullable = false)
    Account targetAccount;

    @Column
    BigDecimal amount;

    /**
     * Currency of transaction. Must match currency of target account.
     */
    @Column
    String currency;

    /**
     * Currency rate that was applied while converting amount to withdraw from source account
     */
    @Column(name = "source_currency_rate")
    BigDecimal sourceCurrencyRate;

    /**
     * Amount withdrawn from source account in source account's currency
     */
    @Column(name = "source_amount")
    BigDecimal sourceAmount;

    @Column(name = "source_balance_after")
    BigDecimal sourceBalanceAfter;

    @Column(name = "target_balance_after")
    BigDecimal targetBalanceAfter;

}
