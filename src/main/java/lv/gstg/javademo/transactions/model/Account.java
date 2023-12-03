package lv.gstg.javademo.transactions.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
public class Account extends BaseEntity {

    @Column(name = "client_id")
    Long clientId;

    @Column(name = "account_nr")
    String accountNr;

    @Column
    String currency;

    @Column
    BigDecimal balance;

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", version=" + version +
                ", clientId=" + clientId +
                ", accountNr='" + accountNr + '\'' +
                ", currency='" + currency + '\'' +
                ", balance=" + balance +
                '}';
    }
}
