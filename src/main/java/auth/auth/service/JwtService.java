package auth.auth.service;

import auth.auth.model.entity.User;
import io.jsonwebtoken.Claims;

public interface JwtService {

    String signAccessToken(User user);

    String signRefreshToken(User user, Long tokenId);

    Long extractTokenIdFromRefresh(String token);

    boolean validateAccessToken(String token);

    Claims extractClaimsFromAccess(String token);
}