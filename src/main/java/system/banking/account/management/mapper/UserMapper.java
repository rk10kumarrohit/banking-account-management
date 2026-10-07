package system.banking.account.management.mapper;

import org.springframework.stereotype.Component;
import system.banking.account.management.dto.UserResponse;
import system.banking.account.management.entity.User;

@Component
public class UserMapper {
    public UserResponse toResponse(User user) {
        return new UserResponse(user.getId(),
                user.getUsername(),
                user.getRole());
    }
}
