package system.banking.account.management.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import system.banking.account.management.entity.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("""
SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
    FROM Account a 
    WHERE a.account_number = :accountNumber
""")
    Boolean existsByAccount_number(@Param("accountNumber") String accountNumber) ;

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("Select a from Account a where a.id = :id")
    Optional<Account> findByIdWithLock(@Param("id") Long id);

    @Query("Select a from Account a where a.balance >= :minimumBalance")
    List<Account> findMinimumBalanceAccounts(@Param("minimumBalance") BigDecimal minimumBalance);
}
