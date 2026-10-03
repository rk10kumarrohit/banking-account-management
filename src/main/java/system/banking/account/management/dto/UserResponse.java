package system.banking.account.management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import system.banking.account.management.entity.Role;

@Getter
@Setter
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private Role role;
}
