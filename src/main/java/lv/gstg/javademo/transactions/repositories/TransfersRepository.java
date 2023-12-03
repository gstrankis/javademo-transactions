package lv.gstg.javademo.transactions.repositories;

import lv.gstg.javademo.transactions.model.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransfersRepository extends JpaRepository<Transfer, Long> {

}
