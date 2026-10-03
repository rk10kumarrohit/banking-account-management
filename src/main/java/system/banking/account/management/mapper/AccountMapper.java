package system.banking.account.management.mapper;

import org.springframework.stereotype.Component;
import system.banking.account.management.dto.AccountRequest;
import system.banking.account.management.dto.AccountResponse;
import system.banking.account.management.entity.Account;

@Component
public class AccountMapper {
    public AccountResponse toResponse(Account account) {
        return new AccountResponse(account.getId(),
                account.getAccount_number(),
                account.getAccount_holder_name(),
                account.getBalance());
    }
    public Account toEntity(AccountRequest accountRequest) {
        Account account = new Account();
        account.setAccount_number(accountRequest.getAccount_number());
        account.setAccount_holder_name(accountRequest.getAccount_holder_name());
        account.setBalance(accountRequest.getBalance());
        return account;
    }
}
