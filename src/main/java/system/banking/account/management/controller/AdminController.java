package system.banking.account.management.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import system.banking.account.management.dto.RoleRequest;
import system.banking.account.management.dto.StatusRequest;
import system.banking.account.management.dto.UserResponse;
import system.banking.account.management.entity.Role;
import system.banking.account.management.entity.User;
import system.banking.account.management.service.UserService;

import java.util.List;

@Tag(
        name = "Admin"
)
@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/test")
    public String adminTest() {
        return "Admin login Successful";
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getUsers() {
            return ResponseEntity.ok(userService.getUsers());
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<UserResponse> updateUserRole(@PathVariable("id") Long id,
            @RequestBody RoleRequest role) {
            return ResponseEntity.ok(userService.updateUserRole(id,role));
    }

    @PutMapping("/users/{id}/status")
    public ResponseEntity<UserResponse> updateUserStatus(@PathVariable("id") Long id,
                                                     @RequestBody StatusRequest statusRequest) {
        return ResponseEntity.ok(userService.updateUserStatus(id,statusRequest));
    }
}
