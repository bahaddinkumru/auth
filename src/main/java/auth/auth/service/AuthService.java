package auth.auth.service;

import auth.auth.model.dto.request.LoginRequest;
import auth.auth.model.dto.response.TokenResponse;

public interface AuthService {
    TokenResponse login(LoginRequest request, String ipAddress, String userAgent);

    TokenResponse refresh(String refreshToken);

    void logout(String refreshToken);
}
