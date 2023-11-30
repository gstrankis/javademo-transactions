package lv.gstg.javademo.transactions.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
@Getter
@Setter
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @Version
    int version;
    @Column(name = "client_id")
    Long clientId;
    String currency;
    BigDecimal balance;
}
