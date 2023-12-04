package lv.gstg.javademo.transactions.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Transaction describes a deposition(credit) or withdrawal(debit) of funds to/from the {@link Account}
 * on the basis of {@link Transfer} operation.<br>
 * Transaction amount is in account's currency.
 */
@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction extends BaseEntity {

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transfer_id", nullable = false)
    Transfer transfer;

    @Column
    @Enumerated(EnumType.STRING)
    TxType txType;

    @Column
    BigDecimal amount;

    @Column(name = "balance_after")
    BigDecimal balanceAfter;

    @Override
    public String toString() {
        return "Transaction{" +
                "createdAt=" + createdAt +
                ", account=" + (account == null ? "null" : account.getId()) +
                ", transfer=" + (transfer == null ? "null" : transfer.getId()) +
                ", txType=" + txType +
                ", amount=" + amount +
                ", balanceAfter=" + balanceAfter +
                ", id=" + id +
                ", version=" + version +
                '}';
    }
}
