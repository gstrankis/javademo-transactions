package lv.gstg.javademo.transactions.repositories;

import lv.gstg.javademo.transactions.model.Transaction;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionsRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t WHERE t.account.id=:accountId ORDER BY createdAt DESC, id DESC")
    List<Transaction> findAllByAccountId(@Param("accountId") Long accountIds);

    @Query("SELECT t FROM Transaction t WHERE t.account.id=:accountId ORDER BY createdAt DESC, id DESC")
    List<Transaction> findAllByAccountIdLimited(@Param("accountId") Long accountIds, Limit limit);

    @Query("SELECT t FROM Transaction t WHERE t.account.id=:accountId ORDER BY createdAt DESC, id DESC")
    List<Transaction> findAllByAccountIdPageable(@Param("accountId") Long accountIds, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT t.* FROM transactions t" +
            " WHERE account_id=:accountId" +
            " ORDER BY created_at DESC, id DESC" +
            " LIMIT :limit OFFSET :offset")
    List<Transaction> findAllByAccountIdUsingNativeQuery(@Param("accountId") Long accountId, @Param("offset") int offset, @Param("limit") int limit);

}
