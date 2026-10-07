package system.banking.account.management.dto;

import system.banking.account.management.entity.Role;

public class RoleRequest {

    private Role role;
    public Role getRole() {
        return this.role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
