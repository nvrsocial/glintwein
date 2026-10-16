package com.nvrsocial.taskflow.user;

import com.nvrsocial.taskflow.user.dto.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toResponse(User user){
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail());
    }
}
