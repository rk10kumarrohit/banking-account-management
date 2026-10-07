package system.banking.account.management.exception;

public class TransactionLimitException extends RuntimeException{
    public TransactionLimitException(String message) {
        super(message);
    }
}
