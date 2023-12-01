package lv.gstg.javademo.transactions.repositories;

import lv.gstg.javademo.transactions.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionsRepository extends JpaRepository<Transaction, Long> {

}
