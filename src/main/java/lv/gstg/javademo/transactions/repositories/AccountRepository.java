package lv.gstg.javademo.transactions.repositories;

import lv.gstg.javademo.transactions.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

}
