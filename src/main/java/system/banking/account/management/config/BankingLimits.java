package system.banking.account.management.config;

import java.math.BigDecimal;

public final class BankingLimits {

    private BankingLimits() {

    }
    public static final BigDecimal MAX_WITHDRAWL = new BigDecimal("50000");
    public static final BigDecimal MAX_TRANSFER = new BigDecimal("100000");
    public static final BigDecimal MINIMUM_BALANCE = new BigDecimal("1000");
}
