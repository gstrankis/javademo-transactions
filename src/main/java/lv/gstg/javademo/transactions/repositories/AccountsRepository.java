package lv.gstg.javademo.transactions.repositories;

import jakarta.persistence.LockModeType;
import lv.gstg.javademo.transactions.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface AccountsRepository extends JpaRepository<Account, Long> {

    List<Account> findAllByClientId(Long clientId);

    @Query("SELECT a from Account a WHERE a.id = :accountId")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Account> findByIdAndLockForUpdate(@Param("accountId") Long accountId);

    @Query("SELECT a from Account a WHERE a.id in (:accountIds)")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Account> findAllByIdAndLockForUpdate(@Param("accountIds") Set<Long> accountIds);

}
