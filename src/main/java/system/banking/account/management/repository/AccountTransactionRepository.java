package system.banking.account.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import system.banking.account.management.entity.Account;
import system.banking.account.management.entity.AccountTransaction;

import java.util.List;

@Repository
public interface AccountTransactionRepository extends JpaRepository<AccountTransaction, Long> {
    List<AccountTransaction> findByAccountOrderByTransactionDateDesc(Account account);
}
