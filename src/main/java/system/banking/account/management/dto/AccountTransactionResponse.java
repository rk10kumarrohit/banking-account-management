package system.banking.account.management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import system.banking.account.management.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class AccountTransactionResponse {
    private Long id;
    private BigDecimal amount;
    private LocalDateTime transactionDate;
    private TransactionType transactionType;
}
