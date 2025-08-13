package org.example.bankcard_systems.dto.admin;

import lombok.Getter;
import lombok.Setter;
import org.example.bankcard_systems.model.Role;

import java.util.Set;

@Getter @Setter
public class UserDto {
    private Long id;
    private String username;
    private Set<Role> roles;
}