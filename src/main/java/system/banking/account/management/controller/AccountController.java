package system.banking.account.management.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import system.banking.account.management.dto.AccountRequest;
import system.banking.account.management.dto.AccountResponse;
import system.banking.account.management.dto.AccountTransactionResponse;
import system.banking.account.management.dto.TransactionRequest;
import system.banking.account.management.service.AccountService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/accounts")
class AccountController {
    private final AccountService accountService;
    public AccountController(AccountService accountService) {
        this.accountService =  accountService;
    }

    @PostMapping
    public AccountResponse createAccount(@Valid @RequestBody AccountRequest accountRequest) {
            return accountService.createAccuont(accountRequest);
    }

    @GetMapping("/{Id}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable Long Id) {
        return ResponseEntity.ok(accountService.getAccount(Id));
    }

    @GetMapping(params = "page")
    public Page<AccountResponse> getAllAccounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page,size);
        return accountService.getAllAccounts(pageable);
    }

    @PutMapping("/{Id}")
    public ResponseEntity<AccountResponse> updateAccount(@PathVariable Long Id,@Valid @RequestBody AccountRequest accountRequest) {
        return ResponseEntity.ok(accountService.updateAccount(Id,accountRequest));
    }

    @PostMapping("/{Id}/withdraw")
    public ResponseEntity<AccountResponse> withdraw(@PathVariable Long Id,@Valid @RequestBody TransactionRequest transactionRequest) {
        return ResponseEntity.ok(accountService.withdraw(Id, transactionRequest));
    }

    @PostMapping("/{Id}/deposit")
    public ResponseEntity<AccountResponse> deposit(@PathVariable Long Id, @Valid @RequestBody TransactionRequest transactionRequest) {
        return ResponseEntity.ok(accountService.deposit(Id, transactionRequest));
    }

    @DeleteMapping("/delete/{Id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long Id) {
        accountService.deleteAccount(Id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(params = "minimumBalance")
    public List<AccountResponse> minimumBalanceAccounts(@RequestParam BigDecimal minimumBalance) {
        return accountService.minimumBalanceAccounts(minimumBalance);
    }

    @GetMapping("/{id}/transactions")
    public List<AccountTransactionResponse> getTransactions(@PathVariable Long id) {
        return  accountService.getTransactions(id);
    }
}
