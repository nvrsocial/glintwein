package com.nvrsocial.glintwein.user.service;

import com.nvrsocial.glintwein.common.exception.UserNotFoundException;
import com.nvrsocial.glintwein.user.entity.User;
import com.nvrsocial.glintwein.user.repository.UserRepository;
import com.nvrsocial.glintwein.user.component.UserMapper;
import com.nvrsocial.glintwein.user.dto.UserResponse;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserResponse getCurrentUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        return userMapper.toResponse(user);
    }
}
