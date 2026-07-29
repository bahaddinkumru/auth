package auth.auth.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import auth.auth.exception.BusinessException;
import auth.auth.exception.ErrorCode;
import auth.auth.mapper.UserMapper;
import auth.auth.model.dto.request.UserRegisterRequest;
import auth.auth.model.dto.response.UserResponse;
import auth.auth.model.entity.User;
import auth.auth.model.enums.Role;
import auth.auth.repository.UserRepository;
import auth.auth.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponse createUser(UserRegisterRequest request) {
        String normalizedEmail = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : "";

        if (userRepository.existsByEmail(normalizedEmail) || userRepository.existsByEmail(request.getEmail()))
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);

        User user = userMapper.toEntity(request);
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ROLE_USER);

        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }
}
