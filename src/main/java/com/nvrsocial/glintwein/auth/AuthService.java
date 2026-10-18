package com.nvrsocial.glintwein.auth;

import com.nvrsocial.glintwein.auth.dto.LoginRequest;
import com.nvrsocial.glintwein.auth.dto.LoginResponse;
import com.nvrsocial.glintwein.auth.dto.RegisterRequest;
import com.nvrsocial.glintwein.common.exception.EmailOrPasswordNotFound;
import com.nvrsocial.glintwein.common.exception.UserAlreadyExistsException;
import com.nvrsocial.glintwein.security.JwtService;
import com.nvrsocial.glintwein.user.User;
import com.nvrsocial.glintwein.user.UserMapper;
import com.nvrsocial.glintwein.user.UserRepository;
import com.nvrsocial.glintwein.user.dto.UserResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public UserResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        User user = new User();

        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        userRepository.save(user);

        return userMapper.toResponse(user);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new EmailOrPasswordNotFound("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new EmailOrPasswordNotFound("Invalid email or password");
        }

        String token = jwtService.generateToken(user);

        return new LoginResponse(token);
    }
}
