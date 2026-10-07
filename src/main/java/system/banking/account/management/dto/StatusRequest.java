package system.banking.account.management.dto;

import system.banking.account.management.entity.User;
import system.banking.account.management.entity.UserStatus;

public class StatusRequest {
    private UserStatus status;
     public UserStatus getStatus() {
         return this.status;
     }
     public void setStatus(UserStatus status) {
         this.status = status;
     }
}
