package auth.auth.repository;

import auth.auth.model.entity.RefreshToken;
import auth.auth.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.List;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    List<RefreshToken> findByUserAndIsRevokedFalse(User user);

    @Modifying
    @Query("UPDATE RefreshToken r SET r.isRevoked = true, r.revokedAt = CURRENT_TIMESTAMP WHERE r.user.id = :userId AND r.isRevoked = false")
    void revokeAllUserTokens(@Param("userId") Long userId);

    @Modifying
    void deleteByExpiresAtBefore(Instant now);
}