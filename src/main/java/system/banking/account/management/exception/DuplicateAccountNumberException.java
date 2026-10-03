package system.banking.account.management.exception;

public class DuplicateAccountNumberException extends  RuntimeException {
    public DuplicateAccountNumberException(String message) {
        super(message);
    }
}
