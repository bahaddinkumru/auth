package auth.auth.service;

import auth.auth.model.dto.response.TokenResponse;
import auth.auth.model.entity.User;

public interface TokenService {
    TokenResponse generateTokens(User user, String ipAddress, String userAgent);

    TokenResponse refreshTokens(String refreshToken);

    void revokeToken(String refreshToken);

    void revokeAllUserTokens(Long userId);

    void cleanupExpiredTokens();
}
