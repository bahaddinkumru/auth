package auth.auth.service;

import auth.auth.model.dto.request.UserRegisterRequest;
import auth.auth.model.dto.response.UserResponse;

public interface UserService {
    UserResponse createUser(UserRegisterRequest request);
}
