package system.banking.account.management.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransferRequest {

    @NotNull(message = "Destination account is required")
    private Long toAccountId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01" , message = "Amount must be greater than zero")
    private BigDecimal amount;
}
