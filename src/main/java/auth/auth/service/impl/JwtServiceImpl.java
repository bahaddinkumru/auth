package auth.auth.service.impl;

import auth.auth.config.JwtProperties;
import auth.auth.exception.BusinessException;
import auth.auth.exception.ErrorCode;
import auth.auth.model.entity.User;
import auth.auth.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final JwtProperties jwtProperties;

    @Override
    public String signAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole().name());
        claims.put("type", "access");

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getId().toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getAccess().getExpirationMs()))
                .signWith(getSignInKey(jwtProperties.getAccess().getSecret()))
                .compact();
    }

    @Override
    public String signRefreshToken(User user, Long tokenId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("type", "refresh");
        claims.put("tokenId", tokenId);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getId().toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getRefresh().getExpirationMs()))
                .signWith(getSignInKey(jwtProperties.getRefresh().getSecret()))
                .compact();
    }

    @Override
    public Long extractTokenIdFromRefresh(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSignInKey(jwtProperties.getRefresh().getSecret()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            if (!"refresh".equals(claims.get("type")))
                throw new BusinessException(ErrorCode.INVALID_TOKEN, "Geçersiz token tipi (Access token kullanılamaz)");

            Number tokenIdObj = claims.get("tokenId", Number.class);
            if (tokenIdObj == null)
                throw new BusinessException(ErrorCode.INVALID_TOKEN, "Token payload içerisinde tokenId bulunamadı");

            return tokenIdObj.longValue();
        } catch (ExpiredJwtException e) {
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
    }

    @Override
    public boolean validateAccessToken(String token) {
        try {
            Claims claims = extractClaimsFromAccess(token);
            return "access".equals(claims.get("type"));
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Claims extractClaimsFromAccess(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey(jwtProperties.getAccess().getSecret()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey(String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}