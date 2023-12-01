package lv.gstg.javademo.transactions.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@ToString
@Builder
public class Account extends BaseEntity {

    @Column(name = "client_id")
    Long clientId;

    @Column(name = "account_nr")
    String accountNr;

    @Column
    String currency;

    @Column
    BigDecimal balance;
}
