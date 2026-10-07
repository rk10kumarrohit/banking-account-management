package system.banking.account.management.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import system.banking.account.management.entity.Account;
import system.banking.account.management.entity.AccountTransaction;
import system.banking.account.management.entity.TransactionType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AccountTransactionRepository extends JpaRepository<AccountTransaction, Long> {

    Page<AccountTransaction> findByAccountAndTransactionDateBetweenOrderByTransactionDateDesc(
            Account account,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            Pageable pageable);
}
