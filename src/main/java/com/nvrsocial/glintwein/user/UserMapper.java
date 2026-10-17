package com.nvrsocial.glintwein.user;

import com.nvrsocial.glintwein.user.dto.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toResponse(User user){
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail());
    }
}
