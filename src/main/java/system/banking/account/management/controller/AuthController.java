package system.banking.account.management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import system.banking.account.management.dto.LoginRequest;
import system.banking.account.management.dto.LoginResponse;
import system.banking.account.management.dto.UserRequest;
import system.banking.account.management.dto.UserResponse;
import system.banking.account.management.service.AuthService;

@Tag(
        name = "Login/Register"
)
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
            summary = "Register",
            description = "Register user/admin"
    )
    @ApiResponse(
            responseCode = "200",
            description = "User registered"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Validation error"
    )
    @ApiResponse(
            responseCode = "409",
            description = "Username already exists"
    )

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody UserRequest userRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.register(userRequest));
    }

    @Operation(
            summary = "Login",
            description = "Login"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid request"
    )
    @ApiResponse(
            responseCode = "403",
            description = "Invalid credentials"
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(authService.login(loginRequest));
    }
}
