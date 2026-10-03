package system.banking.account.management.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import system.banking.account.management.dto.LoginRequest;
import system.banking.account.management.dto.LoginResponse;
import system.banking.account.management.dto.UserRequest;
import system.banking.account.management.dto.UserResponse;
import system.banking.account.management.entity.User;
import system.banking.account.management.exception.DuplicateUserException;
import system.banking.account.management.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder= passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }
    public UserResponse register(UserRequest userRequest) {

        if(userRepository.existsByUsername(userRequest.getUsername())) {
            throw new DuplicateUserException();
        };

        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setRole(userRequest.getRole());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        userRepository.save(user);

        return new UserResponse(user.getId(),user.getUsername(),user.getRole());
    }

    public LoginResponse login(LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                        loginRequest.getPassword());
        authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        return new LoginResponse(jwtService.generateToken(loginRequest.getUsername()));
    }
}
