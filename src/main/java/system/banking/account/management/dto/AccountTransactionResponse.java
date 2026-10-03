package system.banking.account.management.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class AccountTransactionResponse {
    private Long id;
    private BigDecimal amount;
    private LocalDateTime transactionDate;
    private String transactionType;
}
