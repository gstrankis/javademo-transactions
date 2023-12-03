package lv.gstg.javademo.transactions.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity represents Transfer operation withdrawing funds from source account and putting into target account.
 */
@Entity
@Table(name = "transfers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transfer extends BaseEntity {

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_account_id", nullable = false)
    Account sourceAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_account_id", nullable = false)
    Account targetAccount;

    @Column
    BigDecimal amount;

    /**
     * Currency of the transfer.
     */
    @Column
    String currency;

    /**
     * Currency rate that was applied while converting amount to withdraw from source account
     */
    @Column(name = "source_currency_rate")
    BigDecimal sourceCurrencyRate;

    /**
     * Currency rate that was applied while converting amount to transfer to target account
     */
    @Column(name = "target_currency_rate")
    BigDecimal targetCurrencyRate;

    @Override
    public String toString() {
        return "Transfer{" +
                "createdAt=" + createdAt +
                ", sourceAccount=" + (sourceAccount == null ? "null" : sourceAccount.getId()) +
                ", targetAccount=" + (targetAccount == null ? "null" : targetAccount.getId()) +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", sourceCurrencyRate=" + sourceCurrencyRate +
                ", targetCurrencyRate=" + targetCurrencyRate +
                ", id=" + id +
                ", version=" + version +
                '}';
    }
}
