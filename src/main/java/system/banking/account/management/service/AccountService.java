package system.banking.account.management.service;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import system.banking.account.management.dto.AccountRequest;
import system.banking.account.management.dto.AccountResponse;
import system.banking.account.management.dto.AccountTransactionResponse;
import system.banking.account.management.dto.TransactionRequest;
import system.banking.account.management.entity.Account;
import system.banking.account.management.entity.AccountTransaction;
import system.banking.account.management.entity.TransactionType;
import system.banking.account.management.exception.AccountNotFoundException;
import system.banking.account.management.exception.DuplicateAccountNumberException;
import system.banking.account.management.exception.InsufficientBalanceException;
import system.banking.account.management.mapper.AccountMapper;
import system.banking.account.management.repository.AccountRepository;
import system.banking.account.management.repository.AccountTransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final AccountTransactionRepository accountTransactionRepository;

    public AccountService(AccountRepository accountRepository,AccountMapper accountMapper, AccountTransactionRepository accountTransactionRepository) {
        this.accountRepository = accountRepository;
        this.accountMapper =accountMapper;
        this.accountTransactionRepository = accountTransactionRepository;
    }
    public AccountResponse createAccuont(AccountRequest accountRequest) {

        Account  account = accountMapper.toEntity(accountRequest);

        if(accountRepository.existsByAccount_number(account.getAccount_number())){
            throw new DuplicateAccountNumberException("Account number already exists: " + account.getAccount_number());
        }

        Account savedAccount = accountRepository.save(account);

        return accountMapper.toResponse(savedAccount);
    }

    public Page<AccountResponse> getAllAccounts(Pageable pageable) {

        return  accountRepository.findAll(pageable)
                .map(accountMapper::toResponse);

    }

    public AccountResponse getAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() ->
                    new AccountNotFoundException("Account not Found with id: " +accountId)
                );
        return accountMapper.toResponse(account);
    }
    @Transactional
    public AccountResponse updateAccount(Long accountId, AccountRequest accountRequest) {
        Account savedAccount = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found for id: " + accountId));

        savedAccount.setAccount_number(accountRequest.getAccount_number());
        savedAccount.setAccount_holder_name(accountRequest.getAccount_holder_name());
        savedAccount.setBalance(accountRequest.getBalance());

        Account updateAccount = accountRepository.save(savedAccount);

        return accountMapper.toResponse(updateAccount);
    }

    @Transactional
    public AccountResponse withdraw(Long Id, TransactionRequest transactionRequest) {
        Account account = accountRepository.findById(Id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found for Id: "+ Id));

        BigDecimal currentBalance =account.getBalance();
        BigDecimal withdrawlAmount = transactionRequest.getAmount();

        if(withdrawlAmount.compareTo(currentBalance) > 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }
        account.setBalance(currentBalance.subtract(withdrawlAmount));

        //Account updatedAccount = accountRepository.save(account);

        AccountTransaction accountTransaction = new AccountTransaction();
        accountTransaction.setAccount(account);
        accountTransaction.setTransactionType(TransactionType.WITHDRAW);
        accountTransaction.setAmount(transactionRequest.getAmount());
        accountTransaction.setTransactionDate(LocalDateTime.now());

        accountTransactionRepository.save(accountTransaction);

        return accountMapper.toResponse(accountTransaction.getAccount());
    }

    @Transactional
    public AccountResponse deposit(Long Id, TransactionRequest transactionRequest) {
        Account account  = accountRepository.findById(Id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found for Id: " + Id));

        account.setBalance(account.getBalance().add(transactionRequest.getAmount()));
        //accountRepository.save(account);

        AccountTransaction accountTransaction = new AccountTransaction();
        accountTransaction.setAccount(account);
        accountTransaction.setTransactionType(TransactionType.DEPOSIT);
        accountTransaction.setAmount(transactionRequest.getAmount());
        accountTransaction.setTransactionDate(LocalDateTime.now());

        accountTransactionRepository.save(accountTransaction);

        return accountMapper.toResponse(account);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAccount(Long Id) {
        Account account = accountRepository.findById(Id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found for Id: "+ Id));
        accountRepository.delete(account);
    }
    public List<AccountResponse> minimumBalanceAccounts(BigDecimal amount) {
            return accountRepository.findAll().stream()
                    .filter(account -> account.getBalance().compareTo(amount)>=0)
                    .map(accountMapper::toResponse)
                    .toList();
    }

    public List<AccountTransactionResponse> getTransactions(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() ->
                     new AccountNotFoundException("Account not found for id: "+ id)
                    );
        List<AccountTransaction> accountTransaction =  accountTransactionRepository.findByAccountOrderByTransactionDateDesc(account);
        return accountTransaction.stream()
                .map((e1) -> {
                    AccountTransactionResponse accountTransactionResponse = new AccountTransactionResponse();
                    accountTransactionResponse.setId(e1.getId());
                    accountTransactionResponse.setAmount(e1.getAmount());
                    accountTransactionResponse.setTransactionDate(e1.getTransactionDate());
                    accountTransactionResponse.setTransactionType(e1.getTransactionType().name());
                    return accountTransactionResponse;
                })
                .toList();
    }
}
