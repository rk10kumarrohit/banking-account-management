package system.banking.account.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import system.banking.account.management.entity.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("""
SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
    FROM Account a 
    WHERE a.account_number = :accountNumber
""")
    Boolean existsByAccount_number(@Param("accountNumber") String accountNumber) ;
}
