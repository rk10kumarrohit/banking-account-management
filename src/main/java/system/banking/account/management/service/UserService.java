package system.banking.account.management.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import system.banking.account.management.dto.RoleRequest;
import system.banking.account.management.dto.StatusRequest;
import system.banking.account.management.dto.UserResponse;
import system.banking.account.management.entity.Role;
import system.banking.account.management.entity.User;
import system.banking.account.management.entity.UserStatus;
import system.banking.account.management.exception.UserNotFoundException;
import system.banking.account.management.mapper.UserMapper;
import system.banking.account.management.repository.UserRepository;

import java.util.List;

@Service public class UserService {

    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;

    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse).toList();
    }

    public UserResponse updateUserRole(Long id, RoleRequest role) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with id: " + id));

        user.setRole(role.getRole());
        userRepository.save(user);
        return userMapper.toResponse(user);
    }

    public UserResponse updateUserStatus(Long id, StatusRequest statusRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with id: " + id));
        user.setStatus(statusRequest.getStatus());
        userRepository.save(user);
        return userMapper.toResponse(user);
    }
}
