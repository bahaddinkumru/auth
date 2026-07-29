package auth.auth.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import auth.auth.exception.BusinessException;
import auth.auth.exception.ErrorCode;
import auth.auth.model.dto.request.LoginRequest;
import auth.auth.model.dto.response.TokenResponse;
import auth.auth.model.entity.User;
import auth.auth.repository.UserRepository;
import auth.auth.service.AuthService;
import auth.auth.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Override
    public TokenResponse login(LoginRequest request, String ipAddress, String userAgent) {
        String normalizedEmail = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : "";

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseGet(() -> userRepository.findByEmail(request.getEmail())
                        .orElseThrow(() -> {
                            log.warn("Giriş başarısız: E-posta adresi veritabanında bulunamadı -> '{}'",
                                    request.getEmail());
                            return new BusinessException(ErrorCode.BAD_CREDENTIALS);
                        }));

        boolean isPasswordMatch = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!isPasswordMatch) {
            log.warn("Giriş başarısız: Kullanıcı mevcut fakat şifre eşleşmedi -> '{}'", request.getEmail());
            throw new BusinessException(ErrorCode.BAD_CREDENTIALS);
        }

        log.info("Kullanıcı başarıyla giriş yaptı -> '{}'", user.getEmail());
        return tokenService.generateTokens(user, ipAddress, userAgent);
    }

    @Override
    public TokenResponse refresh(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank())
            throw new BusinessException(ErrorCode.INVALID_TOKEN, "Refresh token bulunamadı!");

        return tokenService.refreshTokens(refreshToken);
    }

    @Override
    public void logout(String refreshToken) {
        if (refreshToken != null && !refreshToken.isBlank())
            tokenService.revokeToken(refreshToken);
    }

}
