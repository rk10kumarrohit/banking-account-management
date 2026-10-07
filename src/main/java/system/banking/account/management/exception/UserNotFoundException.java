package system.banking.account.management.exception;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String meassage) {
        super(
                meassage
        );
    }
}
