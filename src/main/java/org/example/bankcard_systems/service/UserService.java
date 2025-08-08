package org.example.bankcard_systems.service;

import org.example.bankcard_systems.model.User;


public interface UserService {

    User getByUsername(String username);

    User getById(Long userId);

}
