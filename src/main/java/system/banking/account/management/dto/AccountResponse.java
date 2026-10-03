package system.banking.account.management.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AccountResponse {
    private Long Id;
    private String account_number;
    private String account_holder_name;
    private BigDecimal balance;
    public AccountResponse(Long Id, String account_number, String account_holder_name,BigDecimal balance){
        this.Id = Id;
        this.account_number= account_number;
        this.account_holder_name= account_holder_name;
        this.balance =balance;
    }
}
