package system.banking.account.management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Builder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import system.banking.account.management.dto.*;
import system.banking.account.management.entity.TransactionType;
import system.banking.account.management.service.AccountService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Tag(
        name = "Account Management",
        description = "APIs for managing bank accounts"
)
@RestController
@RequestMapping("/accounts")
class AccountController {
    private final AccountService accountService;
    public AccountController(AccountService accountService) {
        this.accountService =  accountService;
    }

    @Operation(
            summary = "Create a bank account",
            description = "Creates a bank account"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Account Created Successfully"
    )
    @ApiResponse(
            responseCode ="400",
            description = "Invalid account details"
    )
    @ApiResponse(
            responseCode = "409",
            description = "Account number already exists"
    )
    @PostMapping
    public AccountResponse createAccount(@Valid @RequestBody AccountRequest accountRequest) {
            return accountService.createAccuont(accountRequest);
    }

    @Operation(
            summary = "Get account details",
            description = "Get account details"
    )
    @ApiResponse(
            responseCode ="200",
            description = "Account fetched successfully"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Account not found"
    )
    @GetMapping("/{Id}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable Long Id) {
        return ResponseEntity.ok(accountService.getAccount(Id));
    }

    @Operation(
            summary = "Get account details list",
            description = "Get account details list"
    )
    @ApiResponse(
            responseCode ="200",
            description = "Account list fetched successfully"
    )
    @ApiResponse(
            responseCode ="400",
            description = "Invalid account details"
    )
    @ApiResponse(
            responseCode = "404",
            description = "No accounts present"
    )
    @GetMapping(params = "page")
    public Page<AccountResponse> getAllAccounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page,size);
        return accountService.getAllAccounts(pageable);
    }

    @Operation(
            summary = "Modify account details",
            description = "Modifies an account "
    )
    @ApiResponse(
            responseCode ="200",
            description = "Account modified successfully"
    )
    @ApiResponse(
            responseCode ="400",
            description = "Invalid account details"
    )
    @ApiResponse(
            responseCode = "404",
            description = "No accounts present"
    )
    @PutMapping("/{Id}")
    public ResponseEntity<AccountResponse> updateAccount(@PathVariable Long Id,@Valid @RequestBody AccountRequest accountRequest) {
        return ResponseEntity.ok(accountService.updateAccount(Id,accountRequest));
    }

    @Operation(
            summary = "Withdraw",
            description = "Withdraws amount from an account"
    )
    @ApiResponse(
            responseCode ="200",
            description = "Withdraw successful"
    )
    @ApiResponse(
            responseCode ="400",
            description = "Invalid amount"
    )
    @ApiResponse(
            responseCode = "404",
            description = "No accounts present"
    )
    @ApiResponse(
            responseCode = "409",
            description = "Concurrent modification"
    )
    @PostMapping("/{Id}/withdraw")
    public ResponseEntity<AccountResponse> withdraw(@PathVariable Long Id,@Valid @RequestBody TransactionRequest transactionRequest) {
        return ResponseEntity.ok(accountService.withdraw(Id, transactionRequest));
    }

    @Operation(
            summary = "Deposit",
            description = "Deposit amount in an  account"
    )
    @ApiResponse(
            responseCode ="200",
            description = "Deposit successful"
    )
    @ApiResponse(
            responseCode ="400",
            description = "Invalid amount"
    )
    @ApiResponse(
            responseCode = "404",
            description = "No accounts present"
    )
    @ApiResponse(
            responseCode = "409",
            description = "Concurrent modification"
    )
    @PostMapping("/{Id}/deposit")
    public ResponseEntity<AccountResponse> deposit(@PathVariable Long Id, @Valid @RequestBody TransactionRequest transactionRequest) {
        return ResponseEntity.ok(accountService.deposit(Id, transactionRequest));
    }

    @Operation(
            summary = "Delete account",
            description = "Delete account"
    )
    @ApiResponse(
            responseCode ="204",
            description = "Account deleted successfully"
    )
    @ApiResponse(
            responseCode = "404",
            description = "No accounts present"
    )
    @ApiResponse(
            responseCode = "409",
            description = "User doesn't have ADMIN role"
    )
    @DeleteMapping("/delete/{Id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long Id) {
        accountService.deleteAccount(Id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Fetch list of accounts with minimum balance",
            description = "Fetches list of account with minimum balance"
    )
    @ApiResponse(
            responseCode ="200",
            description = "Fetched accounts successfully"
    )
    @ApiResponse(
            responseCode ="400",
            description = "Invalid account details"
    )
    @ApiResponse(
            responseCode = "404",
            description = "No accounts present"
    )
    @GetMapping("/minimumBalance")
    public List<AccountResponse> minimumBalanceAccounts(@RequestParam(required = true) BigDecimal minimumBalance) {
        return accountService.minimumBalanceAccounts(minimumBalance);
    }

    @Operation(
            summary = "Fetch all the transactions of a account",
            description = "Fetches all the transactions of a account"
    )
    @ApiResponse(
            responseCode ="200",
            description = "Fetched transactions successfully"
    )
    @ApiResponse(
            responseCode ="400",
            description = "Invalid account details"
    )
    @ApiResponse(
            responseCode = "404",
            description = "No accounts present"
    )
    @GetMapping("/{id}/transactions")
    public ResponseEntity<Page<AccountTransactionResponse>> getTransactions(
            @PathVariable Long id,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "transactionDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        return ResponseEntity.ok(
                accountService.getTransactions(
                        id, type, fromDate, toDate,
                        page, size, sortBy, direction));
    }

    @Operation(

            summary = "Account transfer",
            description = "Transfers balance from one account to another"
    )
    @PostMapping("/{id}/transfer")
    public ResponseEntity<Void> transfer(
            @PathVariable Long id, @Valid @RequestBody TransferRequest transferRequest
            ) {
        accountService.transfer(id,transferRequest);
        return ResponseEntity.ok().build();
    }
}
