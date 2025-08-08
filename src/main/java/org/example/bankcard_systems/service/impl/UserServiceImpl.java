package org.example.bankcard_systems.service.impl;

import org.example.bankcard_systems.exception.NotFoundException;
import org.example.bankcard_systems.model.User;
import org.example.bankcard_systems.repository.UserRepository;
import org.example.bankcard_systems.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new NotFoundException(""));
    }

    @Override
    public User getById(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException(""));
    }

}
