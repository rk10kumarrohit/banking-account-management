package system.banking.account.management.service;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import system.banking.account.management.config.BankingLimits;
import system.banking.account.management.dto.*;
import system.banking.account.management.entity.Account;
import system.banking.account.management.entity.AccountStatus;
import system.banking.account.management.entity.AccountTransaction;
import system.banking.account.management.entity.TransactionType;
import system.banking.account.management.exception.*;
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
        account.setAccountStatus(AccountStatus.ACTIVE);

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

        validateAccountIsActive(account);
        BigDecimal withDrawlAmount = transactionRequest.getAmount();
        if(withDrawlAmount.compareTo(BankingLimits.MAX_WITHDRAWL) > 0) {
            throw new TransactionLimitException(
                    "Maximum withdrawl limit is " + BankingLimits.MAX_WITHDRAWL
            );
        }

        BigDecimal remainingBalance = account.getBalance().subtract(withDrawlAmount);
        if (remainingBalance.compareTo(BankingLimits.MINIMUM_BALANCE) < 0) {
            throw new TransactionLimitException(
                    "Account must maintain a minimum balance of "
                            + BankingLimits.MINIMUM_BALANCE
            );
        }

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

        validateAccountIsActive(account);

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

            return accountRepository.findMinimumBalanceAccounts(amount)
                    .stream().map(accountMapper::toResponse).toList();
    }

    public Page<AccountTransactionResponse> getTransactions(
            Long accountId,
            TransactionType type,
            LocalDate fromDate,
            LocalDate toDate,
            int page,
            int size,
            String sortBy,
            String direction) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() ->
                     new AccountNotFoundException("Account not found for id: "+ accountId)
                    );
        Sort.Direction sortDirection =
                direction.equalsIgnoreCase("asc")
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC;

        LocalDateTime fromDateTime =
                fromDate != null
                        ? fromDate.atStartOfDay()
                        : null;

        LocalDateTime toDateTime =
                toDate != null
                        ? toDate.plusDays(1).atStartOfDay()
                        : null;
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, sortBy)
        );
        Page<AccountTransaction> transactions = accountTransactionRepository.findByAccountAndTransactionDateBetweenOrderByTransactionDateDesc(account,fromDateTime,toDateTime, pageable);
       return transactions.map(transaction -> new AccountTransactionResponse(
               transaction.getId(),
               transaction.getAmount(),
               transaction.getTransactionDate(),
               transaction.getTransactionType()
       ));
    }

    @Transactional
    public void transfer(Long fromAccountId, TransferRequest transferRequest) {

        Long toAccountId = transferRequest.getToAccountId();
        BigDecimal amount = transferRequest.getAmount();

        if(fromAccountId.equals(toAccountId)) {
            throw new IllegalArgumentException("Source and destination cannot be the same");
        }

        Account firstAccount;
        Account secondAccount;

        if (fromAccountId < toAccountId) {
            firstAccount = accountRepository.findByIdWithLock(fromAccountId).
                    orElseThrow(() -> new AccountNotFoundException("Source account not found: " + fromAccountId));

            secondAccount = accountRepository.findByIdWithLock(transferRequest.getToAccountId())
                    .orElseThrow(() -> new AccountNotFoundException("Destination account not found: " + toAccountId));

        }

        else {
            firstAccount = accountRepository.findByIdWithLock(toAccountId).
                    orElseThrow(() -> new AccountNotFoundException("Source account not found: " + toAccountId));
            secondAccount = accountRepository.findByIdWithLock(fromAccountId).
                    orElseThrow(() -> new AccountNotFoundException("Destination account not found: " + fromAccountId));

        }

        validateAccountIsActive(firstAccount);
        validateAccountIsActive(secondAccount);

        if (amount.compareTo(BankingLimits.MAX_TRANSFER) > 0) {
            throw new TransactionLimitException(
                    "Maximum transfer limit is "
                            + BankingLimits.MAX_TRANSFER
            );
        }

        Account sourceAccount = fromAccountId.equals(firstAccount.getId())
                ? firstAccount :secondAccount;

        Account destinationAccount = toAccountId.equals(firstAccount.getId())
                ? firstAccount : secondAccount;

        BigDecimal remainingBalance =
                sourceAccount.getBalance().subtract(amount);

        if (remainingBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance"
            );
        }

        if (remainingBalance.compareTo(BankingLimits.MINIMUM_BALANCE) < 0) {
            throw new TransactionLimitException(
                    "Source account must maintain a minimum balance of "
                            + BankingLimits.MINIMUM_BALANCE
            );
        }

        //Debit source
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));

        //Credit destination
        destinationAccount.setBalance(destinationAccount.getBalance().add(amount));

        //Create withdrawl transaction
        AccountTransaction withdrawl = new AccountTransaction();

        withdrawl.setAccount(sourceAccount);
        withdrawl.setTransactionType(TransactionType.WITHDRAW);
        withdrawl.setAmount(amount);
        withdrawl.setTransactionDate(LocalDateTime.now());

        //Create deposit transaction
        AccountTransaction deposit = new AccountTransaction();

        deposit.setAccount(destinationAccount);
        deposit.setAmount(amount);
        deposit.setTransactionDate(LocalDateTime.now());
        deposit.setTransactionType(TransactionType.DEPOSIT);

        accountTransactionRepository.save(withdrawl);
        accountTransactionRepository.save(deposit);

    }
    private void validateAccountIsActive(Account account) {
        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new AccountInactiveException(
                    "Transaction not allowed. Account is "
                            + account.getAccountStatus()
            );
        }
    }
}
