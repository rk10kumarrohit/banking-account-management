package system.banking.account.management.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AccountRequest {

    @NotBlank(message = "account number cannot be blank")
    private String account_number;

    @NotBlank(message = "account holder name is required")
    private String account_holder_name;

    @NotNull(message = "balance is required")
    @DecimalMin(value = "0.0" , message = "balance cannot be negative")
    private BigDecimal balance;

}
