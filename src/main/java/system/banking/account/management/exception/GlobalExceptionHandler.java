package system.banking.account.management.exception;

import jakarta.persistence.OptimisticLockException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import system.banking.account.management.dto.ErrorResponse;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<String>> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest httpServletRequest) {
            List<String> errors = ex.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(error -> error.getDefaultMessage())
                    .toList();
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(errors);
    }
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFound(AccountNotFoundException ex, HttpServletRequest httpServletRequest) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse(LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.name(),
                ex.getMessage(), httpServletRequest.getRequestURI()));
    }
    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientBalance(InsufficientBalanceException ex, HttpServletRequest httpServletRequest) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(LocalDateTime.now(),
                        HttpStatus.NOT_FOUND.value(),
                        HttpStatus.BAD_REQUEST.name(),
                        ex.getMessage(),
                        httpServletRequest.getRequestURI()));
    }
    @ExceptionHandler(DuplicateAccountNumberException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateAccountNumber(DuplicateAccountNumberException ex, HttpServletRequest httpServletRequest) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(LocalDateTime.now(),
                        HttpStatus.NOT_FOUND.value(),
                        HttpStatus.BAD_REQUEST.name(),
                        ex.getMessage(),
                        httpServletRequest.getRequestURI()));
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleAccountConcurrentUpdates(ObjectOptimisticLockingFailureException ex, HttpServletRequest httpServletRequest) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(LocalDateTime.now(),
                    HttpStatus.NOT_FOUND.value(),
                    HttpStatus.BAD_REQUEST.name(),
                "Account was modified by another transaction. Please retry.",
                    httpServletRequest.getRequestURI()));
    }

    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateUserNames(DuplicateUserException ex, HttpServletRequest httpServletRequest) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(LocalDateTime.now(),
                        HttpStatus.CONFLICT.value(),
                        HttpStatus.CONFLICT.name(),
                        "User already exists",
                        httpServletRequest.getRequestURI()));

    }
}
