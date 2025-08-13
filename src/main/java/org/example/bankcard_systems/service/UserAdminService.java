package org.example.bankcard_systems.service;

import org.example.bankcard_systems.model.User;
import org.example.bankcard_systems.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface UserAdminService {
    Page<User> getAllUsers(Pageable pageable);
    User getUserById(Long userId);
    User updateUserRoles(Long userId, Set<Role> roles);
    void deleteUser(Long userId);
}