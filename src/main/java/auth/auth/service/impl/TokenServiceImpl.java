package auth.auth.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import auth.auth.config.JwtProperties;
import auth.auth.exception.BusinessException;
import auth.auth.exception.ErrorCode;
import auth.auth.model.dto.response.TokenResponse;
import auth.auth.model.entity.RefreshToken;
import auth.auth.model.entity.User;
import auth.auth.repository.RefreshTokenRepository;
import auth.auth.service.JwtService;
import auth.auth.service.TokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenServiceImpl implements TokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    @Override
    @Transactional
    public TokenResponse generateTokens(User user, String ipAddress, String userAgent) {
        String accessToken = jwtService.signAccessToken(user);
        Instant expiresAt = Instant.now().plus(jwtProperties.getRefresh().getExpirationDays(), ChronoUnit.DAYS);

        RefreshToken newRefreshTokenEntity = RefreshToken.builder()
                .user(user)
                .tokenHash("pending")
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .expiresAt(expiresAt)
                .lastUsedAt(Instant.now())
                .isRevoked(false)
                .build();

        RefreshToken savedToken = refreshTokenRepository.save(newRefreshTokenEntity);

        String refreshTokenString = jwtService.signRefreshToken(user, savedToken.getId());

        String tokenHash = hashToken(refreshTokenString);
        savedToken.setTokenHash(tokenHash);
        refreshTokenRepository.save(savedToken);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenString)
                .build();
    }

    @Override
    @Transactional
    public TokenResponse refreshTokens(String refreshToken) {
        Long tokenId = jwtService.extractTokenIdFromRefresh(refreshToken);

        RefreshToken tokenRecord = refreshTokenRepository.findById(tokenId)
                .orElseThrow(
                        () -> new BusinessException(ErrorCode.INVALID_TOKEN, "Refresh token veritabanında bulunamadı"));

        if (tokenRecord.isRevoked()) {
            log.warn("GÜVENLİK İHLALİ: İptal edilmiş token kullanımı tespit edildi! UserId: {}",
                    tokenRecord.getUser().getId());
            refreshTokenRepository.revokeAllUserTokens(tokenRecord.getUser().getId());
            throw new BusinessException(ErrorCode.TOKEN_REVOKED_REUSE_DETECTED);
        }

        if (Instant.now().isAfter(tokenRecord.getExpiresAt())) {
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED);
        }

        String incomingHash = hashToken(refreshToken);
        if (!incomingHash.equals(tokenRecord.getTokenHash())) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        tokenRecord.setLastUsedAt(Instant.now());
        refreshTokenRepository.save(tokenRecord);

        long daysRemaining = ChronoUnit.DAYS.between(Instant.now(), tokenRecord.getExpiresAt());

        if (daysRemaining <= 1) {
            tokenRecord.setRevoked(true);
            tokenRecord.setRevokedAt(Instant.now());

            TokenResponse newTokens = generateTokens(
                    tokenRecord.getUser(),
                    tokenRecord.getIpAddress(),
                    tokenRecord.getUserAgent());

            Long newTargetId = jwtService.extractTokenIdFromRefresh(newTokens.getRefreshToken());
            tokenRecord.setReplacedByTokenId(newTargetId.toString());

            refreshTokenRepository.save(tokenRecord);
            return newTokens;
        }

        String newAccessToken = jwtService.signAccessToken(tokenRecord.getUser());
        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    @Transactional
    public void revokeToken(String refreshToken) {
        try {
            Long tokenId = jwtService.extractTokenIdFromRefresh(refreshToken);
            refreshTokenRepository.findById(tokenId).ifPresent(token -> {
                token.setRevoked(true);
                token.setRevokedAt(Instant.now());
                refreshTokenRepository.save(token);
            });
        } catch (Exception e) {
            log.warn("Revoke işlemi için geçersiz token gönderildi.");
        }
    }

    @Override
    @Transactional
    public void revokeAllUserTokens(Long userId) {
        refreshTokenRepository.revokeAllUserTokens(userId);
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupExpiredTokens() {
        log.info("Süresi dolmuş token'lar temizleniyor...");
        refreshTokenRepository.deleteByExpiresAtBefore(Instant.now());
        log.info("Temizlik işlemi tamamlandı.");
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algoritması bulunamadı", e);
        }
    }
}
