package org.example.bankcard_systems.mapper.admin;

import org.example.bankcard_systems.dto.admin.UserDto;
import org.example.bankcard_systems.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDto mapToUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRoles(user.getRoles());
        return dto;
    }

    public User mapToUser(UserDto dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setRoles(dto.getRoles());
        return user;
    }
}