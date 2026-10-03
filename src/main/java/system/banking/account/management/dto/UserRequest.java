package system.banking.account.management.dto;

import lombok.Getter;
import lombok.Setter;
import system.banking.account.management.entity.Role;

@Getter
@Setter
public class UserRequest {
    private String username;
    private String password;
    private Role role;
}
